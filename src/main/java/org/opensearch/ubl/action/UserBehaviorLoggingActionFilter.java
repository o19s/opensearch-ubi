/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.ActionRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.ActionFilter;
import org.opensearch.action.support.ActionFilterChain;
import org.opensearch.common.settings.Settings;
import org.opensearch.core.action.ActionListener;
import org.opensearch.core.action.ActionResponse;
import org.opensearch.tasks.Task;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.ubl.HeaderConstants;
import org.opensearch.ubl.backends.Backend;
import org.opensearch.ubl.model.QueryRequest;
import org.opensearch.ubl.model.QueryResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class UserBehaviorLoggingActionFilter implements ActionFilter {

    private static final Logger LOGGER = LogManager.getLogger(UserBehaviorLoggingActionFilter.class);

    private final Backend backend;
    private final Settings settings;
    private final ThreadPool threadPool;

    public UserBehaviorLoggingActionFilter(final Backend backend, final Settings settings, ThreadPool threadPool) {
        this.backend = backend;
        this.settings = settings;
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

                LOGGER.info("Query ID header: " + task.getHeader("query-id"));

                final long startTime = System.currentTimeMillis();

                final String eventStore = task.getHeader(HeaderConstants.EVENT_STORE_HEADER);

                // If there is no event store header we should not continue anything.
                if(eventStore != null && !eventStore.trim().isEmpty()) {

                    // Get the search itself.
                    final SearchRequest searchRequest = (SearchRequest) request;

                    // TODO: Restrict logging to only queries of certain indices specified in the settings.
                    //final List<String> indices = Arrays.asList(searchRequest.indices());
                    //final Set<String> indicesToLog = new HashSet<>(Arrays.asList(settings.get(SettingsConstants.INDEX_NAMES).split(",")));
                    //if(indicesToLog.containsAll(indices)) {

                    // Get all search hits from the response.
                    if (response instanceof SearchResponse) {

                        // Create a UUID for this search request.
                        final String queryId = UUID.randomUUID().toString();

                        // The query will be empty when there is no query, e.g. /_search
                        final String query = searchRequest.source().toString();

                        // Create a UUID for this search response.
                        final String queryResponseId = UUID.randomUUID().toString();

                        final List<String> queryResponseHitIds = new LinkedList<>();

                        final SearchResponse searchResponse = (SearchResponse) response;

                        // Add each hit to the list of query responses.
                        searchResponse.getHits().forEach(hit -> {
                            queryResponseHitIds.add(String.valueOf(hit.docId()));
                        });

                        try {

                            // Persist the query to the backend.
                            backend.persistQuery(eventStore,
                                    new QueryRequest(queryId, query),
                                    new QueryResponse(queryId, queryResponseId, queryResponseHitIds));

                        } catch (Exception ex) {
                            // TODO: Handle this.
                            LOGGER.error("Unable to persist query.", ex);
                        }

                        threadPool.getThreadContext().addResponseHeader("query_id", queryId);

                    }

                    //}

                    final long elapsedTime = System.currentTimeMillis() - startTime;
                    LOGGER.info("UBL search request filter took {} ms", elapsedTime);

                }

                listener.onResponse(response);

            }

            @Override
            public void onFailure(Exception ex) {
                listener.onFailure(ex);
            }

        });

    }

}
