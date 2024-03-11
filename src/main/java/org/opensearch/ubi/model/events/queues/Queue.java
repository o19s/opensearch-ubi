/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.model.events.queues;

import org.opensearch.ubi.model.events.Event;

import java.util.List;

/**
 * A queue that stores events prior to being indexed.
 */
public interface Queue<T> {

    /**
     * Add an {@link Event event} to the queue.
     * @param event The {@link Event event} to add to the queue.
     */
    void add(T event);

    /**
     * Remove all events from the queue.
     */
    void clear();

    /**
     * Get a list of items in the queue.
     * @return A list of items in the queue.
     */
    List<T> get();

    /**
     * Gets the count of items on the queue.
     * @return The count of items on the queue.
     */
    int size();

}
