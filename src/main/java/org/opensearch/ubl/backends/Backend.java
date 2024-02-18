/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl.backends;

import org.opensearch.ubl.model.QueryResponse;
import org.opensearch.ubl.model.QueryRequest;
import org.opensearch.rest.RestChannel;

import java.util.List;

public interface Backend {

    void initialize(final String storeName, RestChannel channel);

    void delete(final String storeName, RestChannel channel);

    void persistEvent(final String storeName, String eventJson);

    void persistQuery(final String storeName, QueryRequest queryRequest, QueryResponse queryResponse) throws Exception;

    List<String> get();

}
