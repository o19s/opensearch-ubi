/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.model;

/**
 * A query received by OpenSearch.
 */
public class QueryRequest {

    private final long timestamp;
    private final String queryId;
    private final String query;
    private final String userId;
    private final String sessionId;

    /**
     * Creates a query request.
     * @param queryId The ID of the query.
     * @param query The query run by OpenSearch.
     * @param userId The ID of the user that initiated the query.
     * @param sessionId The ID of the session under which the query was run.
     */
    public QueryRequest(final String queryId, final String query, final String userId, final String sessionId) {
        this.timestamp = System.currentTimeMillis();
        this.queryId = queryId;
        this.query = query;
        this.userId = userId;
        this.sessionId = sessionId;
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

}
