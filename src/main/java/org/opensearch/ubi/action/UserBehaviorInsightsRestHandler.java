/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.node.NodeClient;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;
import org.opensearch.ubi.HeaderConstants;
import org.opensearch.ubi.backends.Backend;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
                new Route(TRACE, "/_plugins/ubi"),          // for debugging rest weirdness
                new Route(POST, "/_plugins/ubi/{store}")); // Indexes events into the store.
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient nodeClient) {

        LOGGER.log(Level.INFO, "{}: received event", request.method());

        if (request.method() == PUT) {

            final String storeName = request.param("store");

            // Validate the store name.
            if(!backend.validateStoreName(storeName)) {
                return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, "missing store name"));
            }

            LOGGER.info("Creating UBI store {}", storeName);

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

            LOGGER.info("Deleting UBI store {}", storeName);

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

                LOGGER.info("Queuing event for storage into UBI store {}", storeName);
                final String eventJson = request.content().utf8ToString();

                try {

                    final String eventJsonWithTimestamp = setEventTimestamp(eventJson);

                    backend.persistEvent(storeName, eventJsonWithTimestamp);
                    return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "event received"));

                } catch (JsonProcessingException ex) {
                    LOGGER.error("Unable to get/set timestamp on event.", ex);
                    return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, "unable to set event timestamp"));
                }

            } else {
                throw new IllegalArgumentException("Missing event content");
            }

        } else if (request.method() == GET) {

            final Set<String> stores = backend.get();
            final String s = String.join(",", stores);

            return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, s));

        } else if (request.method() == TRACE) {
            LOGGER.warn("TRACE ############################################");
            
            final Map<String, List<String>> headers = request.getHeaders();
            LOGGER.info("Exposed headers: " + String.join(",", headers.keySet()));
            
            List<String> ids = headers.get(HeaderConstants.QUERY_ID_HEADER.toString());
            String queryId = null;
            if(ids == null || ids.size() == 0){
                LOGGER.warn("Null REST parameter: {}. Using default id.", HeaderConstants.QUERY_ID_HEADER);
                queryId = UUID.randomUUID().toString();
            }
            else {
                queryId = ids.get(0); 
            }

            final Set<String> stores = backend.get();
            

            final String s = "query_id:" + queryId + "&stores:" + String.join(",", stores);
            
            BytesRestResponse response = new BytesRestResponse(RestStatus.OK, "application/x-www-form-urlencoded", s);
            response.addHeader("Access-Control-Expose-Headers", "query_id");
            response.addHeader("query_id", queryId);

            return (channel) -> channel.sendResponse(response);
        } 
        else
            LOGGER.warn("Unknown method " + request.method());

        // TODO: Return a list names of all search_relevance stores.
        return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "ok"));

    }

    private String setEventTimestamp(final String eventJson) throws JsonProcessingException {

        final JsonNode rootNode = new ObjectMapper().readTree(eventJson);

        ObjectNode target = (ObjectNode) rootNode;

        // If there is already a timestamp don't overwrite it.
        if(target.get("timestamp") == null || Objects.equals(target.get("timestamp").asText(), "")) {
            target.put("timestamp", System.currentTimeMillis());
        }

        return new ObjectMapper().writeValueAsString(rootNode);

    }

}
