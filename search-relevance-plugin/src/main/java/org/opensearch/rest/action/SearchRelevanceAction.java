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
import org.opensearch.client.node.NodeClient;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.opensearch.rest.RestRequest.Method.GET;
import static org.opensearch.rest.RestRequest.Method.POST;

public class SearchRelevanceAction extends BaseRestHandler {

    private static final Logger LOGGER = LogManager.getLogger(SearchRelevanceAction.class);

    @Override
    public String getName() {
        return "rest_handler_search_relevance";
    }

    @Override
    public List<Route> routes() {
        return unmodifiableList(asList(
                new Route(GET, "/_plugins/search_relevance"),
                new Route(POST, "/_plugins/search_relevance")));
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {

        final boolean valid = request.hasContent();

        if(request.method() == POST) {

            if (request.hasContent()) {
                final String event = request.content().toString();

                // TODO: Process the event.
                LOGGER.log(Level.INFO, event);

                return channel -> {
                    try {
                        channel.sendResponse(SearchRelevanceService.buildResponse(valid));
                    } catch (final Exception e) {
                        channel.sendResponse(new BytesRestResponse(channel, e));
                    }
                };

            }

        }

        // TODO: Do something else.

        return channel -> {
            try {
                channel.sendResponse(SearchRelevanceService.buildResponse(valid));
            } catch (final Exception e) {
                channel.sendResponse(new BytesRestResponse(channel, e));
            }
        };

    }

}
