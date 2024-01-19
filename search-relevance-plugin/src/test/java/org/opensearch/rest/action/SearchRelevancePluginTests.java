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

    public void testBuildSearchRelevanceResponse() {
        assertThat(SearchRelevanceService.buildResponse().content().utf8ToString(), equalTo("Event received"));
    }

}