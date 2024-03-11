/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.events;

/**
 * A client-side event.
 */
public class Event {

    /**
     * The name of the OpenSearch index where this event will be stored.
     */
    private final String indexName;

    /**
     * The event (a JSON string).
     */
    private final String event;

    /**
     * Create a new event.
     * @param indexName The name of the index where this event will be stored.
     * @param event The event (a JSON string).
     */
    public Event(String indexName, String event) {
        this.indexName = indexName;
        this.event = event;
    }

    /**
     * Gets the name of the index where this event is to be stored.
     * @return The name of the index where this event is to be stored.
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * Gets the event.
     * @return The event.
     */
    public String getEvent() {
        return event;
    }

}
