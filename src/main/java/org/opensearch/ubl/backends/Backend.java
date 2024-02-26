/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl.backends;

import org.opensearch.ubl.model.QueryRequest;
import org.opensearch.ubl.model.QueryResponse;

import java.nio.channels.Channel;
import java.util.Set;

public interface Backend {

    void initialize(final String storeName);

    void delete(final String storeName);

    void persistEvent(final String storeName, String eventJson);

    void persistQuery(final String storeName, QueryRequest queryRequest, QueryResponse queryResponse) throws Exception;

    Set<String> get();

    boolean exists(final String storeName);

    boolean validateStoreName(final String storeName);

}
