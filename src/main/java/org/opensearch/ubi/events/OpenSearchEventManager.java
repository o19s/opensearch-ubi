/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.Client;
import org.opensearch.common.xcontent.XContentType;

/**
 * An event manager that inserts events into an OpenSearch index.
 */
public class OpenSearchEventManager extends AbstractEventManager {

    private static final Logger LOGGER = LogManager.getLogger(OpenSearchEventManager.class);

    private final Client client;
    private static OpenSearchEventManager openSearchEventManager;

    private OpenSearchEventManager(Client client) {
        this.client = client;
    }

    @Override
    public void process() {

        if(eventQueue.size() > 0) {

            final BulkRequest bulkRequest = new BulkRequest();
            LOGGER.info("Bulk inserting " + eventQueue.size() + " UBI events");

            for (final Event event : eventQueue.get()) {

                final IndexRequest indexRequest = new IndexRequest(event.getIndexName())
                        .source(event.getEvent(), XContentType.JSON);

                bulkRequest.add(indexRequest);

            }

            eventQueue.clear();
            client.bulk(bulkRequest);

        }

    }

    @Override
    public void add(Event event) {
        eventQueue.add(event);
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
