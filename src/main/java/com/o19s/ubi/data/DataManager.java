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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Base class for managing client-side events and queries.
 */
public abstract class DataManager {

    @SuppressWarnings("unused")
    private final Logger LOGGER = LogManager.getLogger(DataManager.class);

    /**
     * The queue that stores the client-side events.
     */
    protected final BlockingQueue<Event> eventsQueue;

    /**
     * The queue that stores the query requests.
     */
    protected final BlockingQueue<QueryRequest> queryRequestsQueue;

    /**
     * Initialize the base client-side event manager.
     */
    public DataManager() {
        this.eventsQueue = new LinkedBlockingDeque<>();
        this.queryRequestsQueue = new LinkedBlockingDeque<>();
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
