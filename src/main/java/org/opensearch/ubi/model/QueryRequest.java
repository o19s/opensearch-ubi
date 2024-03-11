/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.model;

/**
 * A query received by OpenSearch.
 */
public class QueryRequest {

    private final String storeName;
    private final long timestamp;
    private final String queryId;
    private final String query;
    private final String userId;
    private final String sessionId;
    private final QueryResponse queryResponse;

    /**
     * Creates a query request.
     * @param storeName The name of the UBI store to hold this query request.
     * @param queryId The ID of the query.
     * @param query The query run by OpenSearch.
     * @param userId The ID of the user that initiated the query.
     * @param sessionId The ID of the session under which the query was run.
     * @param queryResponse The {@link QueryResponse} for this query request.
     */
    public QueryRequest(final String storeName, final String queryId, final String query,
                        final String userId, final String sessionId, final QueryResponse queryResponse) {
        this.storeName = storeName;
        this.timestamp = System.currentTimeMillis();
        this.queryId = queryId;
        this.query = query;
        this.userId = userId;
        this.sessionId = sessionId;
        this.queryResponse = queryResponse;
    }

    /**
     * Gets the name of the UBI store.
     * @return The name of the UBI store.
     */
    public String getStoreName() {
        return storeName;
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
     * Gets the query.
     * @return The query.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Gets the user ID.
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the session ID.
     * @return The session ID.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Gets the query response for this query request.
     * @return The {@link QueryResponse} for this query request.
     */
    public QueryResponse getQueryResponse() {
        return queryResponse;
    }

}
