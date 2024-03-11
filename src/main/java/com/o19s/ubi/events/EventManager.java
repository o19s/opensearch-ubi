/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.events;

import com.o19s.ubi.model.QueryRequest;
import com.o19s.ubi.model.events.queues.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.o19s.ubi.events.queues.InternalQueue;

/**
 * Base class for managing client-side events.
 */
public abstract class EventManager {

    @SuppressWarnings("unused")
    private final Logger LOGGER = LogManager.getLogger(EventManager.class);

    /**
     * The {@link Queue queue} that stores the client-side events.
     */
    protected final Queue<Event> eventsQueue;

    /**
     * The {@link Queue queue} that stores the query reqeusts.
     */
    protected final Queue<QueryRequest> queryRequestsQueue;

    /**
     * Initialize the base client-side event manager.
     */
    public EventManager() {
        this.eventsQueue = new InternalQueue<>();
        this.queryRequestsQueue = new InternalQueue<>();
    }

    /**
     * Process the events on the queue by writing them to persistent storage.
     */
    public abstract void processEvents();

    /**
     * Process the queries on the queue by writing them to persistent storage.
     */
    public abstract void processQueries();

    /**
     * Add an event to the queue.
     * @param event A client-side {@link Event event} to be persisted.
     */
    public abstract void add(Event event);

    /**
     * Add an event to the queue.
     * @param queryRequest A {@link QueryRequest} to be persisted.
     */
    public abstract void add(QueryRequest queryRequest);

}
