/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

import org.opensearch.test.OpenSearchTestCase;

import java.util.List;
import java.util.Map;

public class QueryRequestTests extends OpenSearchTestCase {

    public void testSerialization() {

        final QueryResponse queryResponse = new QueryResponse("queryid", "queryrequestid", List.of("12345", "67890"));

        final QueryRequest queryRequest = new QueryRequest("queryid", "userquery", "clientid", "query",
                Map.of("key1", "value1", "key2", "value2"), queryResponse);

        final String json = queryRequest.toString();

    }

}
