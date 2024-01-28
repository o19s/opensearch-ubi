/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.relevance.backends;

import org.opensearch.client.node.NodeClient;
import org.opensearch.rest.RestChannel;

import java.util.List;

public interface Backend {

    void initialize(final String indexName, final NodeClient nodeClient, RestChannel channel);

    void delete(final String indexName, final NodeClient nodeClient, RestChannel channel);

    void persist(final String indexName, String event, final NodeClient nodeClient);

    List<String> get();

}
