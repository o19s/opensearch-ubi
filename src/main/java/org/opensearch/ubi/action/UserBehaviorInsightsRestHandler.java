/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.action;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.node.NodeClient;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;
import org.opensearch.ubi.backends.Backend;

import java.util.List;
import java.util.Set;

import static org.opensearch.rest.RestRequest.Method.*;

public class UserBehaviorInsightsRestHandler extends BaseRestHandler {

    private static final Logger LOGGER = LogManager.getLogger(UserBehaviorInsightsRestHandler.class);

    private final Backend backend;

    public UserBehaviorInsightsRestHandler(final Backend backend) {
        this.backend = backend;
    }

    @Override
    public String getName() {
        return "Search Relevance";
    }

    @Override
    public List<Route> routes() {
        return List.of(
                new Route(PUT, "/_plugins/ubi/{store}"), // Initializes the store.
                new Route(DELETE, "/_plugins/ubi/{store}"), // Deletes a store.
                new Route(GET, "/_plugins/ubi"), // Lists all stores
                new Route(POST, "/_plugins/ubi/{store}")); // Indexes events into the store.
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient nodeClient) {

        LOGGER.log(Level.INFO, "received event");

        if (request.method() == PUT) {

            final String storeName = request.param("store");

            // Validate the store name.
            if(!backend.validateStoreName(storeName)) {
                return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, "missing store name"));
            }

            LOGGER.info("Creating UBL store {}", storeName);

            return (channel) -> {
                /*if(backend.exists(storeName)) {
                    channel.sendResponse(new BytesRestResponse(RestStatus.CONFLICT, "already exists"));
                } else {*/
                    backend.initialize(storeName);
                    channel.sendResponse(new BytesRestResponse(RestStatus.OK, "created"));
                //}
            };

        } else if (request.method() == DELETE) {

            final String storeName = request.param("store");

            // Validate the store name.
            if(!backend.validateStoreName(storeName)) {
                return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, "missing store name"));
            }

            LOGGER.info("Deleting UBL store {}", storeName);

            return (channel) -> {
                backend.delete(storeName);
                channel.sendResponse(new BytesRestResponse(RestStatus.OK, "created"));
            };

        } else if (request.method() == POST) {

            if (request.hasContent()) {

                final String storeName = request.param("store");

                // Make sure the store exists.
                /*if(!backend.exists(storeName)) {
                    return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.NOT_FOUND, "store not found"));
                }*/

                LOGGER.info("Queuing event for storage into UBL store {}", storeName);
                final String eventJson = request.content().utf8ToString();
                backend.persistEvent(storeName, eventJson);

                return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "event received"));

            } else {
                throw new IllegalArgumentException("Missing event content");
            }

        } else if (request.method() == GET) {

            final Set<String> stores = backend.get();
            final String s = String.join(",", stores);

            return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, s));

        }

        // TODO: Return a list names of all search_relevance stores.
        return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "ok"));

    }

}
