/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl.events.queues;

import org.opensearch.action.index.IndexRequest;

import java.util.LinkedList;
import java.util.List;

public class InternalQueue implements EventQueue {

    private static final List<IndexRequest> indexRequests = new LinkedList<>();

    @Override
    public void add(IndexRequest indexRequest) {
        indexRequests.add(indexRequest);
    }

    @Override
    public void clear() {
        indexRequests.clear();
    }

    @Override
    public List<IndexRequest> get() {
        return indexRequests;
    }

    @Override
    public int size() {
        return indexRequests.size();
    }

}
