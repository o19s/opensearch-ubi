/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.backends;

import org.opensearch.ubi.model.QueryResponse;
import org.opensearch.ubi.model.QueryRequest;

import java.util.List;

public interface Backend {

    void initialize(final String storeName);

    void delete(final String storeName);

    void persistEvent(final String storeName, String eventJson);

    void persistQuery(final String storeName, QueryRequest queryRequest, QueryResponse queryResponse) throws Exception;

    List<String> get();

}
