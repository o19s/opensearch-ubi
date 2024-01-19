/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.rest.action;


import org.opensearch.core.rest.RestStatus;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestResponse;

public class SearchRelevanceService {

    public static RestResponse buildResponse(final boolean valid) {

        if(valid) {
            return new BytesRestResponse(RestStatus.OK, "Event received");
        } else {
            return new BytesRestResponse(RestStatus.OK, "Invalid event received");
        }

    }
}
