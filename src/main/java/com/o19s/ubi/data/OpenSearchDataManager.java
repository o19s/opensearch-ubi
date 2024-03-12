/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.data;

import com.o19s.ubi.model.Event;
import com.o19s.ubi.model.QueryRequest;
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
public class OpenSearchDataManager extends DataManager {

    private static final Logger LOGGER = LogManager.getLogger(OpenSearchDataManager.class);

    private final Client client;
    private final int max_items_batch = 1000;
    private static OpenSearchDataManager openSearchEventManager;

    private OpenSearchDataManager(Client client) {
        this.client = client;
    }

    @Override
    public void processEvents() {

        final BulkRequest eventsBulkRequest = new BulkRequest();

        while(eventsQueue.peek() != null && eventsBulkRequest.numberOfActions() <= max_items_batch) {

            final Event event = eventsQueue.remove();

            final IndexRequest indexRequest = new IndexRequest(event.getIndexName())
                    .source(event.getEvent(), XContentType.JSON);

            eventsBulkRequest.add(indexRequest);

        }

        if(eventsBulkRequest.numberOfActions() > 0) {
            client.bulk(eventsBulkRequest);
        }

    }

    @Override
    public void processQueries() {

        final BulkRequest queryRequestsBulkRequest = new BulkRequest();

        while(queryRequestsQueue.peek() != null && queryRequestsBulkRequest.numberOfActions() <= max_items_batch) {

            final QueryRequest queryRequest = queryRequestsQueue.remove();

            LOGGER.trace("Writing query ID {} with response ID {}",
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

        if(queryRequestsBulkRequest.numberOfActions() > 0) {
            client.bulk(queryRequestsBulkRequest);
        }

    }

    @Override
    public void add(final Event event) {
        eventsQueue.add(event);
    }

    @Override
    public void add(final QueryRequest queryRequest) {
        queryRequestsQueue.add(queryRequest);
    }

    /**
     * Gets a singleton instance of the manager.
     * @param client An OpenSearch {@link Client}.
     * @return An instance of {@link OpenSearchDataManager}.
     */
    public static OpenSearchDataManager getInstance(Client client) {
        if(openSearchEventManager == null) {
            openSearchEventManager = new OpenSearchDataManager(client);
        }
        return openSearchEventManager;
    }

}
