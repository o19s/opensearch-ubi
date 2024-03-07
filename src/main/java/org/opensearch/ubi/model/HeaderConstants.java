/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.model;

/**
 * HTTP headers used by the plugin.
 */
public enum HeaderConstants {

    /**
     * The plugin-assigned ID of the query.
     */
    QUERY_ID_HEADER("X-ubi-query-id"),

    /**
     * The name of the UBI store associated with a query.
     */
    EVENT_STORE_HEADER("X-ubi-store"),

    /**
     * The ID of a user performing a query.
     */
    USER_ID_HEADER("X-ubi-user-id"),

    /**
     * A session ID corresponding to the query.
     */
    SESSION_ID_HEADER("X-ubi-session-id");

    private final String header;

    private HeaderConstants(String header) {
        this.header = header;
    }

    /**
     * Gets the string value of the header.
     * @return The string value of the header.
     */
    public String getHeader() {
        return header;
    }

    @Override
    public String toString() {
        return header;
    }

}
