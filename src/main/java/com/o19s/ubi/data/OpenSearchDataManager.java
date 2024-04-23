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
import org.opensearch.action.bulk.BulkItemResponse;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.Client;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.rest.action.RestActionListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An event manager that inserts events into an OpenSearch index.
 */
public class OpenSearchDataManager extends DataManager {

    private static final Logger LOGGER = LogManager.getLogger(OpenSearchDataManager.class);

    private final Client client;
    private final int max_items_batch = 1000;
    private static OpenSearchDataManager openSearchEventManager;

    /**
     * The queue that stores the client-side events.
     */
    protected final BlockingQueue<Event> eventsQueue;

    /**
     * The queue that stores the query requests.
     */
    protected final BlockingQueue<QueryRequest> queryRequestsQueue;

    /**
     * Gets a singleton instance of the manager.
     *
     * @param client An OpenSearch {@link Client}.
     * @return An instance of {@link OpenSearchDataManager}.
     */
    public static OpenSearchDataManager getInstance(Client client) {
        if (openSearchEventManager == null) {
            openSearchEventManager = new OpenSearchDataManager(client);
        }
        return openSearchEventManager;
    }

    private OpenSearchDataManager(Client client) {
        this.client = client;
        this.eventsQueue = new LinkedBlockingQueue<>();
        this.queryRequestsQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void processEvents() {

        try {

            final BulkRequest eventsBulkRequest = new BulkRequest();

            final Collection<Event> events = new LinkedList<>();
            this.eventsQueue.drainTo(events, max_items_batch);
            events.stream()
                    .map(event -> new IndexRequest(event.getIndexName()).source(event.getEvent(), XContentType.JSON))
                    .forEach(eventsBulkRequest::add);

            if (eventsBulkRequest.numberOfActions() > 0) {

                LOGGER.trace("UBI bulk indexing " + eventsBulkRequest.numberOfActions() + " events.");

                client.bulk(eventsBulkRequest, new RestActionListener<>(null) {

                    @Override
                    public void processResponse(final BulkResponse bulkResponse) {

                        if (bulkResponse.hasFailures()) {
                            for (final BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
                                if (bulkItemResponse.isFailed()) {
                                    LOGGER.warn("Unable to insert UBI event: " + bulkItemResponse.getFailureMessage());
                                }
                            }
                        }

                    }
                });
            }

        } catch (Exception ex) {
            LOGGER.error("Error processing the UBI events queue.", ex);
        }

    }

    @Override
    public void processQueries() {

        try {

            final BulkRequest queryRequestsBulkRequest = new BulkRequest();

            final Collection<QueryRequest> queryRequests = new LinkedList<>();
            queryRequestsQueue.drainTo(queryRequests, max_items_batch);

            queryRequests.stream()
                    .map(queryRequest -> new IndexRequest(UbiUtils.getQueriesIndexName(queryRequest.getStoreName()))
                            .source(buildQueryRequestMap(queryRequest), XContentType.JSON))
                    .forEach(queryRequestsBulkRequest::add);

            LOGGER.trace("UBI bulk indexing " + queryRequestsBulkRequest.numberOfActions() + " queries.");

            if (queryRequestsBulkRequest.numberOfActions() > 0) {
                client.bulk(queryRequestsBulkRequest, new RestActionListener<>(null) {

                    @Override
                    public void processResponse(final BulkResponse bulkResponse) {

                        if (bulkResponse.hasFailures()) {
                            for (final BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
                                if (bulkItemResponse.isFailed()) {
                                    LOGGER.warn("Unable to insert UBI query: " + bulkItemResponse.getFailureMessage());
                                }
                            }
                        }

                    }
                });
            }

        } catch (Exception ex) {
            LOGGER.error("Error processing the UBI query requests queue.", ex);
        }

    }

    private Map<String, Object> buildQueryRequestMap(final QueryRequest queryRequest) {

        LOGGER.trace("Writing query ID {} with response ID {}",
                queryRequest.getQueryId(), queryRequest.getQueryResponse().getQueryResponseId());

        // What will be indexed - adheres to the queries-mapping.json
        final Map<String, Object> source = new HashMap<>();
        source.put("timestamp", queryRequest.getTimestamp());
        source.put("query_id", queryRequest.getQueryId());
        source.put("query", queryRequest.getQuery());
        source.put("query_response_id", queryRequest.getQueryResponse().getQueryResponseId());
        source.put("query_response_objects_ids", queryRequest.getQueryResponse().getQueryResponseHitIds());
        source.put("user_id", queryRequest.getUserId());
        source.put("session_id", queryRequest.getSessionId());

        return source;

    }

    @Override
    public void add(final Event event) {
        eventsQueue.add(event);
    }

    @Override
    public void add(final QueryRequest queryRequest) {
        queryRequestsQueue.add(queryRequest);
    }

}
