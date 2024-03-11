/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.events.queues;

import com.o19s.ubi.events.Event;

import java.util.List;

/**
 * A queue that stores events prior to being indexed.
 */
public interface EventQueue {

    /**
     * Add an {@link Event event} to the queue.
     * @param event The {@link Event event} to add to the queue.
     */
    void add(Event event);

    /**
     * Remove all events from the queue.
     */
    void clear();

    /**
     * Get a list of items in the queue.
     * @return A list of items in the queue.
     */
    List<Event> get();

    /**
     * Gets the count of items on the queue.
     * @return The count of items on the queue.
     */
    int size();

}
