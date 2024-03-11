/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.model.events.queues;

import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of {@link Queue} that uses an in-memory list.
 */
public class InternalQueue<T> implements Queue<T> {

    private final List<T> indexRequests = new LinkedList<>();

    @Override
    public void add(T event) {
        indexRequests.add(event);
    }

    @Override
    public void clear() {
        indexRequests.clear();
    }

    @Override
    public List<T> get() {
        return indexRequests;
    }

    @Override
    public int size() {
        return indexRequests.size();
    }

}
