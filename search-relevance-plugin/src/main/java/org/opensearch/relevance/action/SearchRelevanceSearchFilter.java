/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.relevance.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.ActionRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.ActionFilter;
import org.opensearch.action.support.ActionFilterChain;
import org.opensearch.core.action.ActionListener;
import org.opensearch.core.action.ActionResponse;
import org.opensearch.tasks.Task;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SearchRelevanceSearchFilter implements ActionFilter {

    private static final Logger LOGGER = LogManager.getLogger(SearchRelevanceSearchFilter.class);

    public SearchRelevanceSearchFilter() {

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

                // Get the search itself.
                final SearchRequest searchRequest = (SearchRequest) request;

                final List<String> indices = Arrays.asList(searchRequest.indices());

                // TODO: We need to restrict this to only searches of certain indices.
                // TODO: Look up which indices to log from the plugin settings.
                //if(indices.contains("awesome")) {

                    // Create a UUID for this search request.
                    final String searchId = UUID.randomUUID().toString();
                    final String query = searchRequest.source().toString();

                    LOGGER.info("Query: {}", query);
                    LOGGER.info("Query ID: {}", searchId);

                    // Create a UUID for this search response.
                    final String responseId = UUID.randomUUID().toString();

                    // Get all search hits from the response.
                    if (response instanceof SearchResponse) {

                        final SearchResponse sr = (SearchResponse) response;

                        // TODO: Log this search request with this list of hits.

                        sr.getHits().forEach(hit -> {

                            // Put the hitId in the hit so the frontend can access it.
                            LOGGER.info("Search hit id: {}", hit.docId());

                        });

                    }

                //}

                listener.onResponse(response);

            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }

        });

    }

}
