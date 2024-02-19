/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.ubl.events.queues.EventQueue;
import org.opensearch.ubl.events.queues.InternalQueue;

public abstract class AbstractEventManager {

    private final Logger LOGGER = LogManager.getLogger(AbstractEventManager.class);

    protected final EventQueue eventQueue;

    public AbstractEventManager() {
        this.eventQueue = new InternalQueue();
    }

    public abstract void process();

    public abstract void add(Event event);

}
