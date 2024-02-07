/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.relevance.model;

import java.util.List;

public class QueryResponse {

    private String queryResponseId;
    private List<Integer> queryResponseHitIds;

    public QueryResponse(final String queryResponseId, final List<Integer> queryResponseHitIds) {
        this.queryResponseId = queryResponseId;
        this.queryResponseHitIds = queryResponseHitIds;
    }

    public String getQueryResponseId() {
        return queryResponseId;
    }

    public void setQueryResponseId(String queryResponseId) {
        this.queryResponseId = queryResponseId;
    }

    public List<Integer> getQueryResponseHitIds() {
        return queryResponseHitIds;
    }

    public void setQueryResponseHitIds(List<Integer> queryResponseHitIds) {
        this.queryResponseHitIds = queryResponseHitIds;
    }

}