/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

/**
 * A query received by OpenSearch.
 */
public class QueryRequest {

    private final long timestamp;
    private final String queryId;
    private final String userId;
    private final String userQuery;
    private final String query;
    private final QueryResponse queryResponse;

    /**
     * Creates a query request.
     * @param queryId The ID of the query.
     * @param userQuery The user-entered query.
     * @param userId The ID of the user that initiated the query.
     * @param query The raw query.
     * @param queryResponse The {@link QueryResponse} for this query request.
     */
    public QueryRequest(final String queryId, final String userQuery, final String userId, final String query, final QueryResponse queryResponse) {
        this.timestamp = System.currentTimeMillis();
        this.queryId = queryId;
        this.userId = userId;
        this.userQuery = userQuery;
        this.query = query;
        this.queryResponse = queryResponse;
    }

    /**
     * Gets the timestamp.
     * @return The timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the query ID.
     * @return The query ID.
     */
    public String getQueryId() {
        return queryId;
    }

    /**
     * Gets the user query.
     * @return The user query.
     */
    public String getUserQuery() {
        return userQuery;
    }

    /**
     * Gets the user ID.
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the raw query.
     * @return The raw query.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Gets the query response for this query request.
     * @return The {@link QueryResponse} for this query request.
     */
    public QueryResponse getQueryResponse() {
        return queryResponse;
    }

}
