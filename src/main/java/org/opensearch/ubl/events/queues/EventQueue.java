/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl.events.queues;

import org.opensearch.action.index.IndexRequest;
import org.opensearch.ubl.events.Event;

import java.util.List;

public interface EventQueue {

    void add(Event event);

    void clear();

    List<Event> get();

    int size();

}
