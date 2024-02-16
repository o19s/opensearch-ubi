/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl.model;

import java.util.List;

public class QueryResponse {

    private final String queryId;
    private final String queryResponseId;
    private final List<String> queryResponseHitIds;

    public QueryResponse(final String queryId, final String queryResponseId, final List<String> queryResponseHitIds) {
        this.queryId = queryId;
        this.queryResponseId = queryResponseId;
        this.queryResponseHitIds = queryResponseHitIds;
    }

    public String getQueryId() {
        return queryId;
    }

    public String getQueryResponseId() {
        return queryResponseId;
    }

    public List<String> getQueryResponseHitIds() {
        return queryResponseHitIds;
    }

}