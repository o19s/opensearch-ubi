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
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.tasks.Task;

public class SearchRelevanceSearchFilter implements ActionFilter {

    private static final Logger LOGGER = LogManager.getLogger(SearchRelevanceSearchFilter.class);

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> void apply(
            Task task, String action, Request request,
            ActionListener<Response> listener, ActionFilterChain<Request, Response> chain) {

        if (!(request instanceof SearchRequest)) {
            chain.proceed(task, action, request, listener);
            return;
        }

        chain.proceed(task, action, request, new ActionListener<>() {

            @Override
            public void onResponse(Response response) {

                // Get the search itself.
                final SearchRequest searchRequest = (SearchRequest) request;
                final SearchSourceBuilder ssb = searchRequest.source();
                LOGGER.info("Search Source Builder: {}", ssb);

                // Get all search hits from the response.
                if(response instanceof SearchResponse) {
                   final SearchResponse sr = (SearchResponse) response;
                   sr.getHits().forEach(hit -> LOGGER.info("hit id: {}", hit.docId()));
                }

                listener.onResponse(response);

            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }

        });

    }

}