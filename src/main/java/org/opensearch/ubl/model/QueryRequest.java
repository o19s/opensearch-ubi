/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl.model;

public class QueryRequest {

    private final long timestamp;
    private final String queryId;
    private final String query;

    public QueryRequest(final String queryId, final String query) {
        this.timestamp = System.currentTimeMillis();
        this.queryId = queryId;
        this.query = query;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getQueryId() {
        return queryId;
    }

    public String getQuery() {
        return query;
    }

}
