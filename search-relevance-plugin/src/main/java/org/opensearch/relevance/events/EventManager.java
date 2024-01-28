/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.relevance.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.Client;
import org.opensearch.relevance.events.queues.EventQueue;
import org.opensearch.relevance.events.queues.InternalQueue;

import java.util.TimerTask;

public class EventManager {

    private static final Logger LOGGER = LogManager.getLogger(EventManager.class);

    private final EventQueue eventQueue;
    private final Client client;
    private static EventManager eventManager;

    private EventManager(Client client) {
        this.client = client;
        this.eventQueue = new InternalQueue();
    }

    public void process() {

        if(eventQueue.size() > 0) {

            final BulkRequest bulkRequest = new BulkRequest();
            LOGGER.info("Bulk inserting " + eventQueue.size() + " search relevance events");

            for (final IndexRequest indexRequest : eventQueue.get()) {
                bulkRequest.add(indexRequest);
            }

            eventQueue.clear();
            client.bulk(bulkRequest);

        }

    }

    public static EventManager getInstance(Client client) {
        if(eventManager == null) {
            eventManager = new EventManager(client);
        }
        return eventManager;
    }

    public void addIndexRequest(IndexRequest request) {
        eventQueue.add(request);
    }

}
