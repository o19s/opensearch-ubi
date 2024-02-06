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
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.util.Supplier;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.node.NodeClient;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.common.settings.Setting;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.util.io.Streams;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.relevance.events.EventManager;
import org.opensearch.rest.RestChannel;
import org.opensearch.rest.action.RestToXContentListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OpenSearchBackend implements Backend {

    private static final Logger LOGGER = LogManager.getLogger(OpenSearchBackend.class);

    private static final String MAPPING_FILE = "index-mapping.json";

    public static final int VERSION = 1;
    public static final Setting<Integer> STORE_VERSION_PROP = Setting.intSetting("index.ublstore_version",
            VERSION, -1, Integer.MAX_VALUE, Setting.Property.IndexScope);

    @Override
    public void initialize(final String indexName, final NodeClient nodeClient, final RestChannel channel) {

        // TODO: Determine if already initialized with this index name first.

        LOGGER.info("Creating search relevance index {}", indexName);

        final CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName)
                .mapping(getResourceFile(indexName))
                .settings(getIndexSettings());

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

    private String getResourceFile(String indexName) {
        try (InputStream is = OpenSearchBackend.class.getResourceAsStream(OpenSearchBackend.MAPPING_FILE)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Streams.copy(is, out);
            return out.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error((Supplier<?>) () -> new ParameterizedMessage(
                            "failed to create index [{}] with resource [{}]",
                            indexName, OpenSearchBackend.MAPPING_FILE), e);
            throw new IllegalStateException("failed to create index with resource [" + OpenSearchBackend.MAPPING_FILE + "]", e);
        }
    }

    private static Settings getIndexSettings() {
        return Settings.builder()
                .put(IndexMetadata.INDEX_NUMBER_OF_SHARDS_SETTING.getKey(), 1)
                .put(IndexMetadata.INDEX_AUTO_EXPAND_REPLICAS_SETTING.getKey(), "0-2")
                //.put(STORE_VERSION_PROP.getKey(), VERSION)
                .put(IndexMetadata.SETTING_PRIORITY, Integer.MAX_VALUE)
                .put(IndexMetadata.SETTING_INDEX_HIDDEN, true)
                /*.put(Settings.builder()
                        .loadFromSource(readResourceFile(indexName, ANALYSIS_FILE), XContentType.JSON)
                        .build())*/
                .build();
    }

}
