/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.search.SearchResponseSections;
import org.opensearch.action.search.ShardSearchFailure;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.ubi.ext.UbiParametersExtBuilder;

import java.io.IOException;

public class UbiSearchResponse  extends SearchResponse {

    private static final String EXT_SECTION_NAME = "ext";
    private static final String UBI_QUERY_ID_FIELD_NAME = "query_id";

    private final String queryId;

    public UbiSearchResponse(
            SearchResponseSections internalResponse,
            String scrollId,
            int totalShards,
            int successfulShards,
            int skippedShards,
            long tookInMillis,
            ShardSearchFailure[] shardFailures,
            Clusters clusters,
            String queryId
    ) {
        super(internalResponse, scrollId, totalShards, successfulShards, skippedShards, tookInMillis, shardFailures, clusters);
        this.queryId = queryId;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {

        builder.startObject();
        innerToXContent(builder, params);

        builder.startObject(EXT_SECTION_NAME);
        builder.startObject(UbiParametersExtBuilder.UBI_PARAMETER_NAME);
        builder.field(UBI_QUERY_ID_FIELD_NAME, this.queryId);
        builder.endObject();
        builder.endObject();
        builder.endObject();

        return builder;

    }

}