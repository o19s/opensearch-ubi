/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.backends;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.opensearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.opensearch.action.admin.indices.get.GetIndexRequest;
import org.opensearch.action.admin.indices.get.GetIndexResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.Client;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.util.io.Streams;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.ubi.SettingsConstants;
import org.opensearch.ubi.events.Event;
import org.opensearch.ubi.events.OpenSearchEventManager;
import org.opensearch.ubi.model.QueryRequest;
import org.opensearch.ubi.model.QueryResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OpenSearchBackend implements Backend {

    private static final Logger LOGGER = LogManager.getLogger(OpenSearchBackend.class);

    private static final String EVENTS_MAPPING_FILE = "events-mapping.json";
    private static final String QUERIES_MAPPING_FILE = "queries-mapping.json";

    public static final int VERSION = 1;

    private final Client client;

    private Map<String, Map<String, String>> storeSettings = new HashMap<>();

    public OpenSearchBackend(final Client client) {
        this.client = client;
    }

    @Override
    public Map<String, Map<String, String>> getStoreSettings() {

        final Map<String, Map<String, String>> settings = new HashMap<>();

        // TODO: Get each index for the store.
        final Set<String> stores = get();

        for(final String store : stores) {
            // TODO: Get this index.
            // TODO: Get the settings for this index.
        }

        // TODO: Get the settings for the store.
        return settings;

    }

    @Override
    public void initialize(final String storeName, final String index, final String idField) {

        LOGGER.info("Creating search relevance store {}", storeName);

        final Settings indexSettings = Settings.builder()
                .put(IndexMetadata.INDEX_NUMBER_OF_SHARDS_SETTING.getKey(), 1)
                .put(IndexMetadata.INDEX_AUTO_EXPAND_REPLICAS_SETTING.getKey(), "0-2")
                .put(IndexMetadata.SETTING_PRIORITY, Integer.MAX_VALUE)
                .put(IndexMetadata.SETTING_INDEX_HIDDEN, true)
                .put(SettingsConstants.INDEX, index)
                .put(SettingsConstants.ID_FIELD, index)
                .put(SettingsConstants.VERSION_SETTING, VERSION)
                .build();

        // Create the events index.
        final String eventsIndexName = getEventsIndexName(storeName);

        final CreateIndexRequest createEventsIndexRequest = new CreateIndexRequest(eventsIndexName)
                .mapping(getResourceFile(EVENTS_MAPPING_FILE))
                .settings(indexSettings);

        client.admin().indices().create(createEventsIndexRequest);

        // Create the queries index.
        final String queriesIndexName = getQueriesIndexName(storeName);

        final CreateIndexRequest createQueryIndexRequest = new CreateIndexRequest(queriesIndexName)
                .mapping(getResourceFile(QUERIES_MAPPING_FILE))
                .settings(indexSettings);

        client.admin().indices().create(createQueryIndexRequest);

        // Store the settings.
        final Map<String, String> settings = new HashMap<>();
        settings.put("index", index);
        settings.put("id_field", idField);
        storeSettings.put(storeName, settings);

    }

    @Override
    public void delete(String storeName) {

        LOGGER.info("Deleting search relevance store {}", storeName);

        final String eventsIndexName = getEventsIndexName(storeName);
        final String queriesIndexName = getQueriesIndexName(storeName);
        final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(eventsIndexName, queriesIndexName);

        client.admin().indices().delete(deleteIndexRequest);
        storeSettings.remove(storeName);

    }

    @Override
    public void persistEvent(String storeName, String eventJson) {

        // Add the event for indexing.
        LOGGER.info("Indexing event into {}", storeName);
        final String eventsIndexName = getEventsIndexName(storeName);

        //return (channel) -> client.index(indexRequest, new RestToXContentListener<>(channel));
        final Event event = new Event(eventsIndexName, eventJson);
        OpenSearchEventManager.getInstance(client).add(event);

    }

    @Override
    public void persistQuery(final String storeName, final QueryRequest queryRequest, QueryResponse queryResponse) {

        LOGGER.info("Writing query ID {} with response ID {}", queryRequest.getQueryId(), queryResponse.getQueryResponseId());

        // What will be indexed - adheres to the queries-mapping.json
        final Map<String, Object> source = new HashMap<>();
        source.put("timestamp", queryRequest.getTimestamp());
        source.put("query_id", queryRequest.getQueryId());
        source.put("query", queryRequest.getQuery());
        source.put("query_response_id", queryResponse.getQueryResponseId());
        source.put("query_response_hit_ids", queryResponse.getQueryResponseHitIds());
        source.put("user_id", queryRequest.getUserId());
        source.put("session_id", queryRequest.getSessionId());

        // Get the name of the queries.
        final String queriesIndexName = getQueriesIndexName(storeName);

        // Build the index request.
        final IndexRequest indexRequest = new IndexRequest(queriesIndexName)
                .source(source, XContentType.JSON);

        // TODO: Move this to the queue, too.
        client.index(indexRequest);

    }

    @Override
    public Set<String> get() {

        final GetIndexRequest getIndexRequest = new GetIndexRequest();
        final GetIndexResponse getIndexResponse = client.admin().indices().getIndex(getIndexRequest).actionGet();
        final String[] indexes = getIndexResponse.indices();
        final Set<String> stores = new HashSet<>();

        for(final String index : indexes) {
            LOGGER.info("Index name: " + index);
            if(index.startsWith(".") && index.endsWith("_queries")) {
                stores.add(index);
            }
        }

        return stores;

        //return Collections.emptySet();

    }

    @Override
    public boolean exists(final String storeName) {

        final String indexName = getEventsIndexName(storeName);

        // TODO: This has to run on a non-blocking thread.
        final IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest(indexName);
        final IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(indicesExistsRequest).actionGet();

        return indicesExistsResponse.isExists();

    }

    @Override
    public boolean validateStoreName(final String storeName) {

        // Validate the store name.
        return storeName != null && !storeName.isEmpty();

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
            throw new IllegalStateException("Unable to create index with resource [" + OpenSearchBackend.EVENTS_MAPPING_FILE + "]", e);
        }
    }

}
