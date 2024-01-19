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
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.node.NodeClient;
import org.opensearch.common.xcontent.XContentFactory;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.opensearch.rest.RestRequest.Method.GET;
import static org.opensearch.rest.RestRequest.Method.POST;

public class SearchRelevanceAction extends BaseRestHandler {

    public static final String INDEX_NAME = "searchrelevance";

    private static final Logger LOGGER = LogManager.getLogger(SearchRelevanceAction.class);

    @Override
    public String getName() {
        return "rest_handler_search_relevance";
    }

    @Override
    public List<Route> routes() {
        return List.of(
                new Route(GET, "/_plugins/search_relevance"),
                new Route(POST, "/_plugins/search_relevance"));
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) {

        if(!request.hasContent()) {
            throw new IllegalArgumentException("Missing event content");
        }

        LOGGER.log(Level.INFO, "received event");

        if (request.method() == POST) {

            if (request.hasContent()) {

                // Create the index.
                // TODO: Only need to do this once on init.
                LOGGER.log(Level.INFO, "Creating index");
                final CreateIndexRequest createIndexRequest = new CreateIndexRequest();
                createIndexRequest.index(INDEX_NAME);
                client.admin().indices().create(createIndexRequest);

                // Index the event.
                LOGGER.log(Level.INFO, "Indexing the event");
                final IndexRequest indexRequest = new IndexRequest();
                indexRequest.index(INDEX_NAME);
                indexRequest.source(request.content().utf8ToString(), XContentType.JSON);
                client.index(indexRequest);

                return channel -> {
                    try {
                        channel.sendResponse(SearchRelevanceService.buildResponse());
                    } catch (final Exception e) {
                        channel.sendResponse(new BytesRestResponse(channel, e));
                    }
                };

            }

        }

        // TODO: Do something else.

        return channel -> {
            try {
                channel.sendResponse(SearchRelevanceService.buildResponse());
            } catch (final Exception e) {
                channel.sendResponse(new BytesRestResponse(channel, e));
            }
        };

    }

}
