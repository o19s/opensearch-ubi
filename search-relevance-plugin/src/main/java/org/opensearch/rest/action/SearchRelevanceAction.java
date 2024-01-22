/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.rest.action;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.node.NodeClient;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.RestResponse;

import java.util.List;

import static org.opensearch.rest.RestRequest.Method.*;

public class SearchRelevanceAction extends BaseRestHandler {

    private static final Logger LOGGER = LogManager.getLogger(SearchRelevanceAction.class);

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
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) {

        LOGGER.log(Level.INFO, "received event");

        if (request.method() == PUT) {

            final String indexName = request.param("store");

            LOGGER.log(Level.INFO, "Creating search relevance index {}", indexName);
            final CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            return (channel) -> client.admin().indices().create(createIndexRequest, new RestToXContentListener<>(channel));

        } else if (request.method() == DELETE) {

            final String indexName = request.param("store");

            LOGGER.log(Level.INFO, "Deleting search relevance index {}", indexName);
            final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
            return (channel) -> client.admin().indices().delete(deleteIndexRequest, new RestToXContentListener<>(channel));

        } else if (request.method() == POST) {

            if (request.hasContent()) {

                final String indexName = request.param("store");

                // Index the event.
                LOGGER.log(Level.INFO, "Indexing event into {}", indexName);
                final IndexRequest indexRequest = new IndexRequest(indexName);
                indexRequest.source(request.content().utf8ToString(), XContentType.JSON);
                return (channel) -> client.index(indexRequest, new RestToXContentListener<>(channel));

            } else {
                throw new IllegalArgumentException("Missing event content");
            }

        }

        // TODO: List all search_relevance stores.

        return channel -> {
            try {
                final RestResponse restResponse = new BytesRestResponse(RestStatus.OK, "Event received");
                channel.sendResponse(restResponse);
            } catch (final Exception e) {
                channel.sendResponse(new BytesRestResponse(channel, e));
            }
        };

    }

}
