/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

import java.util.Map;

/**
 * A query received by OpenSearch.
 */
public class QueryRequest {

    private final long timestamp;
    private final String queryId;
    private final String clientId;
    private final String userQuery;
    private final String query;
    private final Map<String, String> queryAttributes;
    private final QueryResponse queryResponse;

    /**
     * Creates a query request.
     * @param queryId The ID of the query.
     * @param userQuery The user-entered query.
     * @param clientId The ID of the client that initiated the query.
     * @param query The raw query.
     * @param queryAttributes An optional map of additional attributes for the query.
     * @param queryResponse The {@link QueryResponse} for this query request.
     */
    public QueryRequest(final String queryId, final String userQuery, final String clientId, final String query,
                        final Map<String, String> queryAttributes, final QueryResponse queryResponse) {
        this.timestamp = System.currentTimeMillis();
        this.queryId = queryId;
        this.clientId = clientId;
        this.userQuery = userQuery;
        this.query = query;
        this.queryAttributes = queryAttributes;
        this.queryResponse = queryResponse;
    }

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();

        // Query Request
        sb.append("{");
        sb.append("\"query_id\": \"").append(queryId).append("\", ");
        sb.append("\"user_query\": \"").append(userQuery).append("\", ");
        sb.append("\"user_id\": \"").append(clientId).append("\", ");

        sb.append("query_attributes: {");
        for(final String key : queryAttributes.keySet()) {
            sb.append("\"").append(key).append("\": ").append("\"").append(queryAttributes.get(key)).append("\", ");
        }
        sb.append("},");

        // Query Response
        sb.append("\"query_response\": {");
        sb.append("\"query_id\": ").append(queryResponse.getQueryId()).append("\", ");
        sb.append("\"query_response_id\": ").append(queryResponse.getQueryResponseId()).append("\", ");
        sb.append("\"query_response_object_ids\": [");
        for(final String objectId : queryResponse.getQueryResponseObjectIds()) {
            sb.append("\"").append(objectId).append("\", ");
        }
        sb.append("],");
        sb.append("}");

        sb.append("}");

        return sb.toString();

    }

    /**
     * Gets the query attributes.
     * @return The query attributes.
     */
    public Map<String, String> getQueryAttributes() {
        return queryAttributes;
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
     * Gets the client ID.
     * @return The client ID.
     */
    public String getClientId() {
        return clientId;
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
