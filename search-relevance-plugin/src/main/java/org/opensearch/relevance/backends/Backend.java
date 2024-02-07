/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.relevance.backends;

import org.opensearch.client.node.NodeClient;
import org.opensearch.relevance.model.QueryRequest;
import org.opensearch.relevance.model.QueryResponse;
import org.opensearch.rest.RestChannel;

import java.util.List;

public interface Backend {

    void initialize(final String storeName, RestChannel channel);

    void delete(final String storeName, RestChannel channel);

    void persistEvent(final String storeName, String event);

    void persistQuery(final String storeName, QueryRequest queryRequest, QueryResponse queryResponse);

    List<String> get();

}
