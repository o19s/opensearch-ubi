/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubl.backends;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.Client;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.util.io.Streams;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.rest.RestChannel;
import org.opensearch.rest.action.RestToXContentListener;
import org.opensearch.ubl.SettingsConstants;
import org.opensearch.ubl.events.EventManager;
import org.opensearch.ubl.model.QueryRequest;
import org.opensearch.ubl.model.QueryResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenSearchBackend implements Backend {

    private static final Logger LOGGER = LogManager.getLogger(OpenSearchBackend.class);

    private static final String EVENTS_MAPPING_FILE = "events-mapping.json";
    private static final String QUERIES_MAPPING_FILE = "queries-mapping.json";

    public static final int VERSION = 1;

    private final Client client;

    public OpenSearchBackend(final Client client) {
        this.client = client;
    }

    @Override
    public void initialize(final String storeName, final RestChannel channel) {

        // TODO: Determine if already initialized with this index name first.
        // TODO: Also need some error handling around this in case one or both of these index creations fail.

        LOGGER.info("Creating search relevance store {}", storeName);

        // Create the events index.
        final String eventsIndexName = getEventsIndexName(storeName);

        final CreateIndexRequest createEventsIndexRequest = new CreateIndexRequest(eventsIndexName)
                .mapping(getResourceFile(EVENTS_MAPPING_FILE))
                .settings(getIndexSettings());

        client.admin().indices().create(createEventsIndexRequest, new RestToXContentListener<>(channel));

        // Create the queries index.
        final String queriesIndexName = getQueriesIndexName(storeName);

        final CreateIndexRequest createQueryIndexRequest = new CreateIndexRequest(queriesIndexName)
                .mapping(getResourceFile(QUERIES_MAPPING_FILE))
                .settings(getIndexSettings());

        client.admin().indices().create(createQueryIndexRequest, new RestToXContentListener<>(channel));

    }

    @Override
    public void delete(String storeName, RestChannel channel) {

        LOGGER.info("Deleting search relevance store {}", storeName);

        final String eventsIndexName = getEventsIndexName(storeName);
        final String queriesIndexName = getQueriesIndexName(storeName);
        final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(eventsIndexName, queriesIndexName);
        client.admin().indices().delete(deleteIndexRequest, new RestToXContentListener<>(channel));

    }

    @Override
    public void persistEvent(String storeName, String event) {

        // Add the event for indexing.
        LOGGER.info("Indexing event into {}", storeName);
        final String eventsIndexName = getEventsIndexName(storeName);
        final IndexRequest indexRequest = new IndexRequest(eventsIndexName)
                .source(event, XContentType.JSON);

        //return (channel) -> client.index(indexRequest, new RestToXContentListener<>(channel));
        EventManager.getInstance(client).addIndexRequest(indexRequest);

    }

    @Override
    public void persistQuery(final String storeName, final QueryRequest queryRequest, QueryResponse queryResponse) throws Exception {

        LOGGER.info("Writing query ID {} with response ID {}", queryRequest.getQueryId(), queryResponse.getQueryResponseId());

        // What will be indexed - adheres to the queries-mapping.json
        final Map<String, Object> source = new HashMap<>();
        source.put("timestamp", queryRequest.getTimestamp());
        source.put("queryId", queryRequest.getQueryId());
        source.put("query", queryRequest.getQuery());
        source.put("queryResponseId", queryResponse.getQueryResponseId());
        source.put("queryResponseHitIds", queryResponse.getQueryResponseHitIds());

        // Get the name of the queries.
        final String queriesIndexName = getQueriesIndexName(storeName);

        // Build the index request.
        final IndexRequest indexRequest = new IndexRequest(queriesIndexName)
                .source(source, XContentType.JSON);

        //return (channel) -> client.index(indexRequest, new RestToXContentListener<>(channel));
        EventManager.getInstance(client).addIndexRequest(indexRequest);

    }

    @Override
    public List<String> get() {
        // TODO: Get the list of initialized stores for the plugin.
        return new ArrayList<>();
    }

    private String getEventsIndexName(final String storeName) {
        return "." + storeName + "_events";
    }

    private String getQueriesIndexName(final String storeName) {
        return "." + storeName + "_queries";
    }

    private String getResourceFile(final String fileName) {
        try (InputStream is = OpenSearchBackend.class.getResourceAsStream(fileName)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Streams.copy(is, out);
            return out.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("failed to create index with resource [" + OpenSearchBackend.EVENTS_MAPPING_FILE + "]", e);
        }
    }

    private Settings getIndexSettings() {
        return Settings.builder()
                .put(IndexMetadata.INDEX_NUMBER_OF_SHARDS_SETTING.getKey(), 1)
                .put(IndexMetadata.INDEX_AUTO_EXPAND_REPLICAS_SETTING.getKey(), "0-2")
                .put(SettingsConstants.VERSION_SETTING, VERSION)
                .put(IndexMetadata.SETTING_PRIORITY, Integer.MAX_VALUE)
                .put(IndexMetadata.SETTING_INDEX_HIDDEN, true)
                .build();
    }

}
