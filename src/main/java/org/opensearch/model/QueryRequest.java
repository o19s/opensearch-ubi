/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.model;

public class QueryRequest {

    private final String queryId;
    private final String query;

    public QueryRequest(final String queryId, final String query) {
        this.queryId = queryId;
        this.query = query;
    }

    public String getQueryId() {
        return queryId;
    }

    public String getQuery() {
        return query;
    }

}
