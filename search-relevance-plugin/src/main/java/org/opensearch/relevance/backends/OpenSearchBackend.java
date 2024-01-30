/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.relevance.backends;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.node.NodeClient;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.relevance.events.EventManager;
import org.opensearch.rest.RestChannel;
import org.opensearch.rest.action.RestToXContentListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenSearchBackend implements Backend {

    private static final Logger LOGGER = LogManager.getLogger(OpenSearchBackend.class);

    @Override
    public void initialize(final String indexName, final NodeClient nodeClient, final RestChannel channel) {

        // TODO: Determine if already initialized with this index name first.

        LOGGER.info("Creating search relevance index {}", indexName);
        final CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        nodeClient.admin().indices().create(createIndexRequest, new RestToXContentListener<>(channel));

    }

    @Override
    public void delete(String indexName, NodeClient nodeClient, RestChannel channel) {

        LOGGER.info("Deleting search relevance index {}", indexName);
        final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        nodeClient.admin().indices().delete(deleteIndexRequest, new RestToXContentListener<>(channel));

    }

    @Override
    public void persist(String indexName, String event, NodeClient nodeClient) {

        // Add the event for indexing.
        LOGGER.info("Indexing event into {}", indexName);
        final IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.source(event, XContentType.JSON);

        //return (channel) -> client.index(indexRequest, new RestToXContentListener<>(channel));
        EventManager.getInstance(nodeClient).addIndexRequest(indexRequest);

    }

    @Override
    public List<String> get() {
        // TODO: Get the list of stores for the plugin.
        return new ArrayList<>();
    }

}
