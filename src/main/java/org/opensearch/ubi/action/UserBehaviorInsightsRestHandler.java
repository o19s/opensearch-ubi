/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.admin.indices.get.GetIndexRequest;
import org.opensearch.action.admin.indices.get.GetIndexResponse;
import org.opensearch.client.node.NodeClient;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.util.io.Streams;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;
import org.opensearch.ubi.SettingsConstants;
import org.opensearch.ubi.backends.Backend;
import org.opensearch.ubi.backends.OpenSearchBackend;
import org.opensearch.ubi.events.Event;
import org.opensearch.ubi.events.OpenSearchEventManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.opensearch.rest.RestRequest.Method.*;

public class UserBehaviorInsightsRestHandler extends BaseRestHandler {

    private static final Logger LOGGER = LogManager.getLogger(UserBehaviorInsightsRestHandler.class);

    private static final String EVENTS_MAPPING_FILE = "events-mapping.json";
    private static final String QUERIES_MAPPING_FILE = "queries-mapping.json";
    public static final int VERSION = 1;
    private Map<String, Map<String, String>> storeSettings = new HashMap<>();

    private final Backend backend;

    public UserBehaviorInsightsRestHandler(final Backend backend) {
        this.backend = backend;
    }

    @Override
    public String getName() {
        return "Search Relevance";
    }

    @Override
    public List<Route> routes() {
        return List.of(
                new Route(PUT, "/_plugins/ubi/{store}"), // Initializes the store.
                new Route(DELETE, "/_plugins/ubi/{store}"), // Deletes a store.
                new Route(GET, "/_plugins/ubi"), // Lists all stores
                new Route(POST, "/_plugins/ubi/{store}")); // Indexes events into the store.
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient nodeClient) {

        LOGGER.log(Level.INFO, "received event");

        if (request.method() == PUT) {

            final String storeName = request.param("store");
            //final String index = request.param("index");
            //final String idField = request.param("id_field");

            // Validate the store name.
            /*if(!backend.validateStoreName(storeName)) {
                return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, "missing store name"));
            } else {*/

                return (channel) -> {
                    LOGGER.info("Creating UBL store {}", storeName);

                /*if(backend.exists(storeName)) {
                    channel.sendResponse(new BytesRestResponse(RestStatus.CONFLICT, "already exists"));
                } else {*/
                    //backend.initialize(storeName, index, idField);

                    //channel.sendResponse(new BytesRestResponse(RestStatus.OK, "created"));
                    //}

                    LOGGER.info("Creating search relevance store {}", storeName);

                    final Settings indexSettings = Settings.builder()
                            .put(IndexMetadata.INDEX_NUMBER_OF_SHARDS_SETTING.getKey(), 1)
                            .put(IndexMetadata.INDEX_AUTO_EXPAND_REPLICAS_SETTING.getKey(), "0-2")
                            .put(IndexMetadata.SETTING_PRIORITY, Integer.MAX_VALUE)
                            .put(IndexMetadata.SETTING_INDEX_HIDDEN, true)
                            //.put(SettingsConstants.INDEX, index)
                            // .put(SettingsConstants.ID_FIELD, index)
                            .put(SettingsConstants.VERSION_SETTING, VERSION)
                            .build();

                    // Create the events index.
                    final String eventsIndexName = getEventsIndexName(storeName);

                    final CreateIndexRequest createEventsIndexRequest = new CreateIndexRequest(eventsIndexName)
                            .mapping(getResourceFile(EVENTS_MAPPING_FILE))
                            .settings(indexSettings);

                    nodeClient.admin().indices().create(createEventsIndexRequest);

                    // Create the queries index.
                    final String queriesIndexName = getQueriesIndexName(storeName);

                    final CreateIndexRequest createQueryIndexRequest = new CreateIndexRequest(queriesIndexName)
                            .mapping(getResourceFile(QUERIES_MAPPING_FILE))
                            .settings(indexSettings);

                    nodeClient.admin().indices().create(createQueryIndexRequest);

                    // Store the settings.
                /*final Map<String, String> settings = new HashMap<>();
                settings.put("index", index);
                settings.put("id_field", idField);
                storeSettings.put(storeName, settings);*/

                };

         //   }

        } else if (request.method() == DELETE) {

            final String storeName = request.param("store");

            // Validate the store name.
            if(!backend.validateStoreName(storeName)) {
                return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, "missing store name"));
            }

            LOGGER.info("Deleting UBL store {}", storeName);

            return (channel) -> {
                //backend.delete(storeName);

                LOGGER.info("Deleting search relevance store {}", storeName);

                final String eventsIndexName = getEventsIndexName(storeName);
                final String queriesIndexName = getQueriesIndexName(storeName);
                final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(eventsIndexName, queriesIndexName);

                nodeClient.admin().indices().delete(deleteIndexRequest);
                //storeSettings.remove(storeName);

                channel.sendResponse(new BytesRestResponse(RestStatus.OK, "deleted"));
            };

        } else if (request.method() == POST) {

            if (request.hasContent()) {

                final String storeName = request.param("store");

                // Make sure the store exists.
                /*if(!backend.exists(storeName)) {
                    return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.NOT_FOUND, "store not found"));
                }*/

                LOGGER.info("Queuing event for storage into UBL store {}", storeName);
                final String eventJson = request.content().utf8ToString();

                try {

                    final String eventJsonWithTimestamp = setEventTimestamp(eventJson);

                    //backend.persistEvent(storeName, eventJsonWithTimestamp);
                    //return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "event received"));

                    // Add the event for indexing.
                    LOGGER.info("Indexing event into {}", storeName);
                    final String eventsIndexName = getEventsIndexName(storeName);

                    //return (channel) -> client.index(indexRequest, new RestToXContentListener<>(channel));
                    final Event event = new Event(eventsIndexName, eventJson);
                    OpenSearchEventManager.getInstance(nodeClient).add(event);

                } catch (JsonProcessingException ex) {
                    LOGGER.error("Unable to get/set timestamp on event.", ex);
                    return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, "unable to set event timestamp"));
                }

            } else {
                throw new IllegalArgumentException("Missing event content");
            }

        } else if (request.method() == GET) {

            //final Set<String> stores = backend.get();
            //final String s = String.join(",", stores);

            //return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, s));

            return (channel) -> {

                final GetIndexRequest getIndexRequest = new GetIndexRequest();
                final GetIndexResponse getIndexResponse = nodeClient.admin().indices().getIndex(getIndexRequest).actionGet();
                final String[] indexes = getIndexResponse.indices();
                final Set<String> stores = new HashSet<>();

                for (final String index : indexes) {
                    LOGGER.info("Index name: " + index);
                    if (index.startsWith(".") && index.endsWith("_queries")) {
                        stores.add(index);
                    }
                }

                //return stores;
                channel.sendResponse(new BytesRestResponse(RestStatus.OK, String.join(",", stores)));

            };

        }

        // TODO: Return a list names of all search_relevance stores.
        return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "ok"));

    }

    private String setEventTimestamp(final String eventJson) throws JsonProcessingException {

        final JsonNode rootNode = new ObjectMapper().readTree(eventJson);

        ObjectNode target = (ObjectNode) rootNode;

        // If there is already a timestamp don't overwrite it.
        if(target.get("timestamp") == null || Objects.equals(target.get("timestamp").asText(), "")) {
            target.put("timestamp", System.currentTimeMillis());
        }

        return new ObjectMapper().writeValueAsString(rootNode);

    }

    private boolean validateStoreName(final String storeName) {

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
            throw new IllegalStateException("Unable to create index with resource [" + fileName + "]", e);
        }
    }

}
