/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.o19s.ubi.UserBehaviorInsightsPlugin;
import com.o19s.ubi.events.Event;
import com.o19s.ubi.model.HeaderConstants;
import com.o19s.ubi.model.SettingsConstants;
import com.o19s.ubi.utils.UbiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.admin.indices.get.GetIndexRequest;
import org.opensearch.action.admin.indices.get.GetIndexResponse;
import org.opensearch.client.node.NodeClient;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;

import com.o19s.ubi.OpenSearchEventManager;

import java.io.IOException;
import java.util.*;

import static org.opensearch.rest.RestRequest.Method.*;

/**
 * The REST handler for User Behavior Insights. The handler provides the
 * REST interface for interacting with UBI stores and for storing client-side events.
 */
public class UserBehaviorInsightsRestHandler extends BaseRestHandler {

    private static final Logger LOGGER = LogManager.getLogger(UserBehaviorInsightsRestHandler.class);

    private static final String EVENTS_MAPPING_FILE = "/events-mapping.json";
    private static final String QUERIES_MAPPING_FILE = "/queries-mapping.json";
    private static final int VERSION = 1;

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
                new Route(TRACE, "/_plugins/ubi"),
                new Route(POST, "/_plugins/ubi/{store}")); // Indexes events into the store.
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient nodeClient) throws IOException {

        final String storeName = restRequest.param("store");

        if (restRequest.method() == PUT) {
            final String index = restRequest.param("index");
            final String idField = restRequest.param("id_field");
            return create(nodeClient, storeName, index, idField);
        } else if(restRequest.method() == DELETE) {
            return delete(nodeClient, storeName);
        } else if(restRequest.method() == POST) {
            return post(nodeClient, storeName, restRequest);
        } else if(restRequest.method() == TRACE) {
            return trace(nodeClient, restRequest);
        }

        return get(nodeClient);

    }

    private RestChannelConsumer trace(NodeClient nodeClient, RestRequest restRequest) {

        return (channel) -> {

            LOGGER.warn("TRACE");

            final Map<String, List<String>> headers = restRequest.getHeaders();
            LOGGER.info("Exposed headers: " + String.join(",", headers.keySet()));

            List<String> ids = headers.get(HeaderConstants.QUERY_ID_HEADER.toString());
            String queryId = null;
            if (ids == null || ids.size() == 0) {
                LOGGER.warn("Null REST parameter: {}. Using default id.", HeaderConstants.QUERY_ID_HEADER);
                queryId = UUID.randomUUID().toString();
            } else {
                queryId = ids.get(0);
            }

            final GetIndexRequest getIndexRequest = new GetIndexRequest();
            final GetIndexResponse getIndexResponse = nodeClient.admin().indices().getIndex(getIndexRequest).actionGet();
            final Set<String> stores = getStoreNames(getIndexResponse.indices());

            final String s = "query_id:" + queryId + "&stores:" + String.join(",", stores);

            BytesRestResponse response = new BytesRestResponse(RestStatus.OK, "application/x-www-form-urlencoded", s);
            response.addHeader("Access-Control-Expose-Headers", "query_id");
            response.addHeader("query_id", queryId);

            channel.sendResponse(response);

        };

    }

    private RestChannelConsumer get(final NodeClient nodeClient) {

        return (channel) -> {

            final GetIndexRequest getIndexRequest = new GetIndexRequest();
            final GetIndexResponse getIndexResponse = nodeClient.admin().indices().getIndex(getIndexRequest).actionGet();
            final Set<String> stores = getStoreNames(getIndexResponse.indices());

            final XContentBuilder builder = XContentType.JSON.contentBuilder();
            builder.startObject().field("stores", stores);
            builder.endObject();

            channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));

        };

    }

    private RestChannelConsumer create(final NodeClient nodeClient, final String storeName, final String index, final String idField) throws IOException {

        LOGGER.info("Creating UBI store [{}] for index [{}] using field [{}]", storeName, index, idField);

        final Settings indexSettings = Settings.builder()
                .put(IndexMetadata.INDEX_NUMBER_OF_SHARDS_SETTING.getKey(), 1)
                .put(IndexMetadata.INDEX_AUTO_EXPAND_REPLICAS_SETTING.getKey(), "0-2")
                .put(IndexMetadata.SETTING_PRIORITY, Integer.MAX_VALUE)
                .put(SettingsConstants.INDEX, index)
                .put(SettingsConstants.ID_FIELD, idField)
                .put(SettingsConstants.VERSION_SETTING, VERSION)
                .build();

        // Create the events index.
        final String eventsIndex = UbiUtils.getEventsIndexName(storeName);
        final CreateIndexRequest createEventsIndexRequest = new CreateIndexRequest(eventsIndex)
                .mapping(UbiUtils.getResourceFile(EVENTS_MAPPING_FILE))
                .settings(indexSettings);

        nodeClient.admin().indices().create(createEventsIndexRequest);

        // Create the queries index.
        final String queriesIndex = UbiUtils.getQueriesIndexName(storeName);
        final CreateIndexRequest createQueriesIndexRequest = new CreateIndexRequest(queriesIndex)
                .mapping(UbiUtils.getResourceFile(QUERIES_MAPPING_FILE))
                .settings(indexSettings);

        nodeClient.admin().indices().create(createQueriesIndexRequest);

        final XContentBuilder builder = XContentType.JSON.contentBuilder();
        builder.startObject().field("status", "initialized");
        builder.endObject();

        return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));

    }

    private RestChannelConsumer post(final NodeClient nodeClient, final String storeName, final RestRequest restRequest) throws IOException {

        try {

            final String eventJson = restRequest.content().utf8ToString();
            final String eventJsonWithTimestamp = setEventTimestamp(eventJson);

            LOGGER.info("Indexing UBI event into store {}", storeName);
            final String eventsIndexName = UbiUtils.getEventsIndexName(storeName);

            final Event event = new Event(eventsIndexName, eventJsonWithTimestamp);
            OpenSearchEventManager.getInstance(nodeClient).add(event);

        } catch (JsonProcessingException ex) {
            LOGGER.error("Unable to get/set timestamp on UBI event.", ex);

            final XContentBuilder builder = XContentType.JSON.contentBuilder();
            builder.startObject().field("error", "unable to set event timestamp");
            builder.endObject();

            return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, builder));
        }

        final XContentBuilder builder = XContentType.JSON.contentBuilder();
        builder.startObject().field("status", "received");
        builder.endObject();

        return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));

    }

    private RestChannelConsumer delete(final NodeClient nodeClient, final String storeName) throws IOException {

        // Delete the events index.
        final DeleteIndexRequest deleteEventsIndexRequest = new DeleteIndexRequest(UbiUtils.getEventsIndexName(storeName));
        nodeClient.admin().indices().delete(deleteEventsIndexRequest);

        // Delete the queries index.
        final DeleteIndexRequest deleteQueriesIndexRequest = new DeleteIndexRequest(UbiUtils.getQueriesIndexName(storeName));
        nodeClient.admin().indices().delete(deleteQueriesIndexRequest);

        final XContentBuilder builder = XContentType.JSON.contentBuilder();
        builder.startObject().field("status", "deleted");
        builder.endObject();

        // Remove this store's settings from the settings map.
        UserBehaviorInsightsPlugin.storeSettings.entrySet().removeIf(entry -> entry.getKey().startsWith(storeName + "."));

        return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));

    }

    private String setEventTimestamp(final String eventJson) throws JsonProcessingException {

        final JsonNode rootNode = new ObjectMapper().readTree(eventJson);

        final ObjectNode target = (ObjectNode) rootNode;

        // If there is already a timestamp don't overwrite it.
        if(target.get("timestamp") == null || Objects.equals(target.get("timestamp").asText(), "")) {
            target.put("timestamp", System.currentTimeMillis());
        }

        return new ObjectMapper().writeValueAsString(rootNode);

    }

    private Set<String> getStoreNames(String[] indices) {
        final Set<String> stores = new HashSet<>();
        for (final String index : indices) {
            if (index.startsWith(".") && index.endsWith("_events")) {
                stores.add(index.substring(1, index.length() - 7));
            }
        }
        return stores;
    }

    private boolean validateStoreName(final String storeName) {

        // Validate the store name.
        return storeName != null && !storeName.isEmpty();

    }

}
