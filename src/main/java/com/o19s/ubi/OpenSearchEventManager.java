/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi;

import com.o19s.ubi.model.QueryRequest;
import com.o19s.ubi.model.events.Event;
import com.o19s.ubi.model.events.EventManager;
import com.o19s.ubi.utils.UbiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.Client;
import org.opensearch.common.xcontent.XContentType;

import java.util.HashMap;
import java.util.Map;

/**
 * An event manager that inserts events into an OpenSearch index.
 */
public class OpenSearchEventManager extends EventManager {

    private static final Logger LOGGER = LogManager.getLogger(OpenSearchEventManager.class);

    private final Client client;
    private static OpenSearchEventManager openSearchEventManager;

    private OpenSearchEventManager(Client client) {
        this.client = client;
    }

    @Override
    public void process() {

        if(eventsQueue.size() > 0) {

            final BulkRequest eventsBulkRequest = new BulkRequest();
            LOGGER.info("Bulk inserting " + eventsQueue.size() + " UBI events");

            for (final Event event : eventsQueue.get()) {

                final IndexRequest indexRequest = new IndexRequest(event.getIndexName())
                        .source(event.getEvent(), XContentType.JSON);

                eventsBulkRequest.add(indexRequest);

            }

            eventsQueue.clear();
            client.bulk(eventsBulkRequest);

        }

        if(queryRequestQueue.size() > 0) {

            final BulkRequest queryRequestsBulkRequest = new BulkRequest();
            LOGGER.info("Bulk inserting " + queryRequestQueue.size() + " UBI queries");

            for(final QueryRequest queryRequest : queryRequestQueue.get()) {

                LOGGER.info("Writing query ID {} with response ID {}",
                        queryRequest.getQueryId(), queryRequest.getQueryResponse().getQueryResponseId());

                // What will be indexed - adheres to the queries-mapping.json
                final Map<String, Object> source = new HashMap<>();
                source.put("timestamp", queryRequest.getTimestamp());
                source.put("query_id", queryRequest.getQueryId());
                source.put("query", queryRequest.getQuery());
                source.put("query_response_id", queryRequest.getQueryResponse().getQueryResponseId());
                source.put("query_response_hit_ids", queryRequest.getQueryResponse().getQueryResponseHitIds());
                source.put("user_id", queryRequest.getUserId());
                source.put("session_id", queryRequest.getSessionId());

                // Get the name of the queries.
                final String queriesIndexName = UbiUtils.getQueriesIndexName(queryRequest.getStoreName());

                // Build the index request.
                final IndexRequest indexRequest = new IndexRequest(queriesIndexName)
                        .source(source, XContentType.JSON);

                queryRequestsBulkRequest.add(indexRequest);

            }

            queryRequestQueue.clear();
            client.bulk(queryRequestsBulkRequest);

        }

    }

    @Override
    public void add(final Event event) {
        eventsQueue.add(event);
    }

    @Override
    public void add(final QueryRequest queryRequest) {
        queryRequestQueue.add(queryRequest);
    }

    /**
     * Gets a singleton instance of the manager.
     * @param client An OpenSearch {@link Client}.
     * @return An instance of {@link OpenSearchEventManager}.
     */
    public static OpenSearchEventManager getInstance(Client client) {
        if(openSearchEventManager == null) {
            openSearchEventManager = new OpenSearchEventManager(client);
        }
        return openSearchEventManager;
    }

}
