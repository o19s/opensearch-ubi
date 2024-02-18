/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl.events;

public class Event {

    private final String indexName;
    private final String event;

    public Event(String indexName, String event) {
        this.indexName = indexName;
        this.event = event;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getEvent() {
        return event;
    }

}
