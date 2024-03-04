/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.backends;

import org.opensearch.ubi.model.QueryRequest;
import org.opensearch.ubi.model.QueryResponse;

import java.util.Map;
import java.util.Set;

public interface Backend {

    void initialize(final String storeName, final String index, final String idField);

    void delete(final String storeName);

    void persistEvent(final String storeName, String eventJson);

    void persistQuery(final String storeName, QueryRequest queryRequest, QueryResponse queryResponse) throws Exception;

    Set<String> get();

    boolean exists(final String storeName);

    boolean validateStoreName(final String storeName);

    Map<String, Map<String, String>> getStoreSettings();

}
