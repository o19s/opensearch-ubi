/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.action;

import com.o19s.ubi.UserBehaviorInsightsPlugin;
import com.o19s.ubi.data.DataManager;
import com.o19s.ubi.data.OpenSearchDataManager;
import com.o19s.ubi.model.HeaderConstants;
import com.o19s.ubi.model.QueryRequest;
import com.o19s.ubi.model.QueryResponse;
import com.o19s.ubi.model.SettingsConstants;
import com.o19s.ubi.utils.UbiUtils;
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
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.action.ActionListener;
import org.opensearch.core.action.ActionResponse;
import org.opensearch.index.IndexNotFoundException;
import org.opensearch.search.SearchHit;
import org.opensearch.tasks.Task;
import org.opensearch.threadpool.ThreadPool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * An implementation of {@link ActionFilter} that passively listens for OpenSearch
 * queries and persists the queries to the UBI store.
 */
public class UserBehaviorInsightsActionFilter implements ActionFilter {

    private static final Logger LOGGER = LogManager.getLogger(UserBehaviorInsightsActionFilter.class);

    private final Client client;
    private final ThreadPool threadPool;
    private final DataManager dataManager;

    /**
     * Creates a new filter.
     * @param client An OpenSearch {@link Client}.
     * @param threadPool The OpenSearch {@link ThreadPool}.
     */
    public UserBehaviorInsightsActionFilter(Client client, ThreadPool threadPool) {
        this.client = client;
        this.threadPool = threadPool;
        this.dataManager = OpenSearchDataManager.getInstance(client);
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
                    final String storeName = getHeaderValue(HeaderConstants.EVENT_STORE_HEADER, "", task);
                    final String userId = getHeaderValue(HeaderConstants.USER_ID_HEADER, "", task);
                    final String sessionId = getHeaderValue(HeaderConstants.SESSION_ID_HEADER, "", task);

                    // If there is no event store header, ignore this search.
                    if(!"".equals(storeName)) {

                        final String index = getStoreSettings(storeName, SettingsConstants.INDEX);
                        final String idField = getStoreSettings(storeName, SettingsConstants.KEY_FIELD);

                        LOGGER.debug("Using key_field [{}] of index [{}] for UBI query.", idField, index);

                        // Only consider this search if the index being searched matches the store's index setting.
                        if (Arrays.asList(searchRequest.indices()).contains(index)) {

                            // The query will be empty when there is no query, e.g. /_search
                            final String query = searchRequest.source().toString();

                            // Create a UUID for this search response.
                            final String queryResponseId = UUID.randomUUID().toString();

                            final List<String> queryResponseHitIds = new LinkedList<>();
                            final SearchResponse searchResponse = (SearchResponse) response;

                            // Add each hit to the list of query responses.
                            for (final SearchHit hit : searchResponse.getHits()) {

                                if (idField == null || "".equals(idField) || idField.equals("null")) {

                                    // Use the _id since there is no key_field setting for this index.
                                    queryResponseHitIds.add(String.valueOf(hit.docId()));

                                } else {

                                    final Map<String, Object> source = hit.getSourceAsMap();
                                    queryResponseHitIds.add((String) source.get(idField));

                                }

                            }

                            final QueryResponse queryResponse = new QueryResponse(queryId, queryResponseId, queryResponseHitIds);
                            final QueryRequest queryRequest = new QueryRequest(storeName, queryId, query, userId, sessionId, queryResponse);

                            // Queue this for writing to the UBI store.
                            dataManager.add(queryRequest);

                            // Add the query_id to the response headers.
                            threadPool.getThreadContext().addResponseHeader("query_id", queryId);

                            //HACK: this should be set in the OpenSearch config (to send to the client code just once),
                            // and not on every single search response,
                            // but that server setting doesn't appear to be exposed.
                            threadPool.getThreadContext().addResponseHeader("Access-Control-Expose-Headers", "query_id");

                        } else {
                            LOGGER.trace("Discarding query for UBI due to index name mismatch.");
                        }

                        final long elapsedTime = System.currentTimeMillis() - startTime;
                        LOGGER.trace("UBI search request filter took {} ms", elapsedTime);

                    } else {
                        LOGGER.trace("Discarding query for UBI due to missing store name.");
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

    private String getStoreSettings(final String storeName, final String setting) {

        final String key = storeName + "." + setting;
        String settingValue = "";

        if(UserBehaviorInsightsPlugin.storeSettings.containsKey(key)) {

            LOGGER.debug("Getting setting " + setting + " for store " + storeName + " from the cache.");
            settingValue = UserBehaviorInsightsPlugin.storeSettings.get(key);

        } else{

            LOGGER.debug("Getting setting " + setting + " for store " + storeName + " from the index.");

            final String queriesIndexName = UbiUtils.getQueriesIndexName(storeName);
            final GetSettingsRequest getSettingsRequest = new GetSettingsRequest().indices(queriesIndexName);

            try {

                final GetSettingsResponse getSettingsResponse = client.admin().indices().getSettings(getSettingsRequest).actionGet();
                final String settingResponse = getSettingsResponse.getSetting(queriesIndexName, setting);

                UserBehaviorInsightsPlugin.storeSettings.put(key, settingResponse);
                settingValue = settingResponse;

            } catch (IndexNotFoundException ex) {
                LOGGER.warn("Unable to get UBI settings for UBI store {} from index {}", storeName, queriesIndexName, ex);
            }

        }

        return settingValue;

    }

    private String getHeaderValue(final HeaderConstants header, final String defaultValue, final Task task) {

        final String value = task.getHeader(header.getHeader());

        if(value == null || value.trim().isEmpty() || value.equals("null")) {
            return defaultValue;
        } else {
            return value;
        }

    }

    /**
     * Persist the query to the UBI store.
     * @param storeName The name of the UBI store.
     * @param queryRequest The {@link QueryRequest} that initiated the query.
     * @param queryResponse The {@link QueryResponse} that resulted from the query.
     */
    private void persistQuery(final String storeName, final QueryRequest queryRequest, QueryResponse queryResponse) {

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
