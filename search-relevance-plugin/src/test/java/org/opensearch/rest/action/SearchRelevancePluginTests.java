/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.rest.action;

import org.opensearch.test.OpenSearchTestCase;

import static org.hamcrest.Matchers.equalTo;

public class SearchRelevancePluginTests extends OpenSearchTestCase {

    public void testBuildValidSearchRelevanceResponse() {
        assertThat(SearchRelevanceService.buildResponse(true).content().utf8ToString(), equalTo("Event received"));
    }

    public void testBuildInvalidSearchRelevanceResponse() {
        assertThat(SearchRelevanceService.buildResponse(false).content().utf8ToString(), equalTo("Invalid event received"));
    }

}