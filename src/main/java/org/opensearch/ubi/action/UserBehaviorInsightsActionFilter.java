/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.ActionRequest;
import org.opensearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.opensearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.ActionFilter;
import org.opensearch.action.support.ActionFilterChain;
import org.opensearch.client.Client;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.action.ActionListener;
import org.opensearch.core.action.ActionResponse;
import org.opensearch.search.SearchHit;
import org.opensearch.tasks.Task;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.ubi.model.HeaderConstants;
import org.opensearch.ubi.model.QueryRequest;
import org.opensearch.ubi.model.QueryResponse;
import org.opensearch.ubi.model.SettingsConstants;
import org.opensearch.ubi.utils.UbiUtils;

import java.io.IOException;
import java.util.*;

public class UserBehaviorInsightsActionFilter implements ActionFilter {

    private static final Logger LOGGER = LogManager.getLogger(UserBehaviorInsightsActionFilter.class);

    private final Client client;
    private final ThreadPool threadPool;

    public UserBehaviorInsightsActionFilter(Client client, ThreadPool threadPool) {
        this.client = client;
        this.threadPool = threadPool;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> void apply(
            Task task, String action, Request request, ActionListener<Response> listener,
            ActionFilterChain<Request, Response> chain) {

        if (!(request instanceof SearchRequest)) {
            chain.proceed(task, action, request, listener);
            return;
        }

        chain.proceed(task, action, request, new ActionListener<>() {

            @Override
            public void onResponse(Response response) {

                final long startTime = System.currentTimeMillis();

                // Get the search itself.
                final SearchRequest searchRequest = (SearchRequest) request;

                // Get all search hits from the response.
                if (response instanceof SearchResponse) {

                    // Get info from the headers.
                    final String queryId = getHeaderValue(HeaderConstants.QUERY_ID_HEADER, UUID.randomUUID().toString(), task);
                    final String eventStore = getHeaderValue(HeaderConstants.EVENT_STORE_HEADER, "default", task);
                    final String userId = getHeaderValue(HeaderConstants.USER_ID_HEADER, "", task);
                    final String sessionId = getHeaderValue(HeaderConstants.SESSION_ID_HEADER, "", task);

                    final String index = "ecommerce";

                    // Only consider this search if the index being searched matches the store's index setting.
                    if (Arrays.asList(searchRequest.indices()).contains(index)) {

                        // The query will be empty when there is no query, e.g. /_search
                        final String query = searchRequest.source().toString();

                        // Create a UUID for this search response.
                        final String queryResponseId = UUID.randomUUID().toString();

                        final List<String> queryResponseHitIds = new LinkedList<>();
                        final SearchResponse searchResponse = (SearchResponse) response;

                        // Get the id_field to use for each result's unique identifier.
                        final String idField = "name";

                        // Add each hit to the list of query responses.
                        for (final SearchHit hit : searchResponse.getHits()) {
                            if ("".equals(idField)) {
                                // Use the _id since there is no id_field setting for this index.
                                queryResponseHitIds.add(String.valueOf(hit.docId()));
                            } else {
                                queryResponseHitIds.add(String.valueOf(hit.field(idField)));
                            }
                        }

                        try {

                            // Persist the query to the backend.
                            persistQuery(eventStore,
                                    new QueryRequest(queryId, query, userId, sessionId),
                                    new QueryResponse(queryId, queryResponseId, queryResponseHitIds));

                        } catch (Exception ex) {
                            // TODO: Handle this.
                            LOGGER.error("Unable to persist query.", ex);
                        }

                        threadPool.getThreadContext().addResponseHeader("query_id", queryId);

                        //}

                        final long elapsedTime = System.currentTimeMillis() - startTime;
                        LOGGER.info("UBI search request filter took {} ms", elapsedTime);

                    }

                }

                listener.onResponse(response);

            }

            @Override
            public void onFailure(Exception ex) {
                listener.onFailure(ex);
            }

        });

    }

    private String getHeaderValue(final HeaderConstants header, final String defaultValue, final Task task) {

        final String value = task.getHeader(header.getHeader());

        if(value == null || value.trim().isEmpty()) {
            return defaultValue;
        } else {
            return value;
        }

    }

    public void persistQuery(final String storeName, final QueryRequest queryRequest, QueryResponse queryResponse) {

        LOGGER.info("Writing query ID {} with response ID {}",
                queryRequest.getQueryId(), queryResponse.getQueryResponseId());

        // What will be indexed - adheres to the queries-mapping.json
        final Map<String, Object> source = new HashMap<>();
        source.put("timestamp", queryRequest.getTimestamp());
        source.put("query_id", queryRequest.getQueryId());
        source.put("query", queryRequest.getQuery());
        source.put("query_response_id", queryResponse.getQueryResponseId());
        source.put("query_response_hit_ids", queryResponse.getQueryResponseHitIds());
        source.put("user_id", queryRequest.getUserId());
        source.put("session_id", queryRequest.getSessionId());

        // Get the name of the queries.
        final String queriesIndexName = UbiUtils.getQueriesIndexName(storeName);

        // Build the index request.
        final IndexRequest indexRequest = new IndexRequest(queriesIndexName)
                .source(source, XContentType.JSON);

        // TODO: Move this to the queue, too.
        client.index(indexRequest);

    }

}
