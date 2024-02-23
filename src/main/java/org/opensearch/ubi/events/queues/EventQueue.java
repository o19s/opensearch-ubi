/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.events.queues;

import org.opensearch.ubi.events.Event;

import java.util.List;

public interface EventQueue {

    void add(Event event);

    void clear();

    List<Event> get();

    int size();

}
