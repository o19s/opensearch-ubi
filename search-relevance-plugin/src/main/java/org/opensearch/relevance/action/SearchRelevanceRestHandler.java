/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.relevance.action;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.node.NodeClient;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.relevance.backends.Backend;
import org.opensearch.relevance.backends.OpenSearchBackend;
import org.opensearch.relevance.events.EventManager;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;

import java.util.List;

import static org.opensearch.rest.RestRequest.Method.*;

public class SearchRelevanceRestHandler extends BaseRestHandler {

    private static final Logger LOGGER = LogManager.getLogger(SearchRelevanceRestHandler.class);

    private final Backend backend;

    public SearchRelevanceRestHandler() {
        this.backend = new OpenSearchBackend();
    }

    @Override
    public String getName() {
        return "Search Relevance";
    }

    @Override
    public List<Route> routes() {
        return List.of(
                new Route(PUT, "/_plugins/search_relevance/{store}"), // Initializes the store.
                new Route(DELETE, "/_plugins/search_relevance/{store}"), // Deletes a store.
                new Route(GET, "/_plugins/search_relevance"), // Lists all stores
                new Route(POST, "/_plugins/search_relevance/{store}")); // Indexes events into the store.
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient nodeClient) {

        LOGGER.log(Level.INFO, "received event");

        if (request.method() == PUT) {

            final String indexName = request.param("store");

            LOGGER.info("Creating search relevance index {}", indexName);
            return (channel) -> backend.initialize(indexName, nodeClient, channel);

        } else if (request.method() == DELETE) {

            final String indexName = request.param("store");

            LOGGER.info("Deleting search relevance index {}", indexName);
            return (channel) -> backend.delete(indexName, nodeClient, channel);

        } else if (request.method() == POST) {

            if (request.hasContent()) {

                final String indexName = request.param("store");

                LOGGER.info("Persisting event into {}", indexName);
                final String event = request.content().utf8ToString();
                backend.persist(indexName, event, nodeClient);

                return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "Event received"));

            } else {
                throw new IllegalArgumentException("Missing event content");
            }

        }

        // TODO: List names of all search_relevance stores.
        return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "ok"));

    }

}
