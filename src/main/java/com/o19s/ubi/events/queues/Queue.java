/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.model.events.queues;

import java.util.List;

/**
 * A queue that stores events prior to being indexed.
 */
public interface Queue<T> {

    /**
     * Add an object to the queue.
     * @param event The object to add to the queue.
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
