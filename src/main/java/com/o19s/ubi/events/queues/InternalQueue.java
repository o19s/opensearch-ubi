/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.events.queues;

import com.o19s.ubi.events.Event;

import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of {@link EventQueue} that uses an in-memory list.
 */
public class InternalQueue implements EventQueue {

    private static final List<Event> indexRequests = new LinkedList<>();

    @Override
    public void add(Event event) {
        indexRequests.add(event);
    }

    @Override
    public void clear() {
        indexRequests.clear();
    }

    @Override
    public List<Event> get() {
        return indexRequests;
    }

    @Override
    public int size() {
        return indexRequests.size();
    }

}
