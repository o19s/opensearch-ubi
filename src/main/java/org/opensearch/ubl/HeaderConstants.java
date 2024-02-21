/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl;

public enum HeaderConstants {

    QUERY_ID_HEADER("X-ubi-query-id"),
    EVENT_STORE_HEADER("X-ubi-store"),
    USER_ID_HEADER("X-ubi-user-id"),
    SESSION_ID_HEADER("X-ubi-session-id");

    private final String header;

    private HeaderConstants(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    @Override
    public String toString() {
        return header;
    }

}
