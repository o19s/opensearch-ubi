/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.o19s.ubi.events.queues.EventQueue;
import com.o19s.ubi.events.queues.InternalQueue;

/**
 * Base class for managing client-side events.
 */
public abstract class AbstractEventManager {

    @SuppressWarnings("unused")
    private final Logger LOGGER = LogManager.getLogger(AbstractEventManager.class);

    /**
     * The {@link EventQueue queue} that stores the client-side events.
     */
    protected final EventQueue eventQueue;

    /**
     * Initialize the base client-side event manager.
     */
    public AbstractEventManager() {
        this.eventQueue = new InternalQueue();
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

}
