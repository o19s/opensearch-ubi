/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl.action;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.node.NodeClient;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.ubl.backends.Backend;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;

import java.util.List;

import static org.opensearch.rest.RestRequest.Method.*;

public class UserBehaviorLoggingRestHandler extends BaseRestHandler {

    private static final Logger LOGGER = LogManager.getLogger(UserBehaviorLoggingRestHandler.class);

    private final Backend backend;

    public UserBehaviorLoggingRestHandler(final Backend backend) {
        this.backend = backend;
    }

    @Override
    public String getName() {
        return "Search Relevance";
    }

    @Override
    public List<Route> routes() {
        return List.of(
                new Route(PUT, "/_plugins/ubl/{store}"), // Initializes the store.
                new Route(DELETE, "/_plugins/ubl/{store}"), // Deletes a store.
                new Route(GET, "/_plugins/ubl"), // Lists all stores
                new Route(POST, "/_plugins/ubl/{store}")); // Indexes events into the store.
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient nodeClient) {

        LOGGER.log(Level.INFO, "received event");

        if (request.method() == PUT) {

            final String storeName = request.param("store");

            LOGGER.info("Creating search relevance index {}", storeName);
            backend.initialize(storeName);
            return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "created"));

        } else if (request.method() == DELETE) {

            final String storeName = request.param("store");

            // TODO: Make sure the store actually exists first.

            LOGGER.info("Deleting search relevance index {}", storeName);
            backend.delete(storeName);
            return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "deleted"));

        } else if (request.method() == POST) {

            if (request.hasContent()) {

                final String storeName = request.param("store");

                // TODO: Make sure the store actually exists first.

                LOGGER.info("Persisting event into {}", storeName);
                final String eventJson = request.content().utf8ToString();
                backend.persistEvent(storeName, eventJson);

                return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "Event received"));

            } else {
                throw new IllegalArgumentException("Missing event content");
            }

        }

        // TODO: Return a list names of all search_relevance stores.
        return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "ok"));

    }

}
