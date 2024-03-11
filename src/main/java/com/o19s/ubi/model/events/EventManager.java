/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.model.events;

import com.o19s.ubi.model.QueryRequest;
import com.o19s.ubi.model.events.queues.InternalQueue;
import com.o19s.ubi.model.events.queues.Queue;

/**
 * Base class for managing client-side events.
 */
public abstract class EventManager {

    /**
     * The {@link Queue queue} that stores the client-side events.
     */
    protected final Queue<Event> eventsQueue;

    /**
     * The {@link Queue queue} that stores the query responses.
     */
    protected final Queue<QueryRequest> queryRequestQueue;

    /**
     * Initialize the base client-side event manager.
     */
    public EventManager() {
        this.eventsQueue = new InternalQueue<>();
        this.queryRequestQueue = new InternalQueue<>();
    }

    /**
     * Process the items on the queue by writing them to persistent storage.
     */
    public abstract void process();

    /**
     * Add an event to the queue.
     * @param event A client-side {@link Event event} to be persisted.
     */
    public abstract void add(Event event);

    /**
     * Add a query request to the queue
     * @param queryRequest A {@link QueryRequest} to be persisted.
     */
    public abstract void add(QueryRequest queryRequest);

}
