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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.admin.indices.get.GetIndexRequest;
import org.opensearch.action.admin.indices.get.GetIndexResponse;
import org.opensearch.client.node.NodeClient;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.common.settings.Settings;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.action.RestToXContentListener;
import org.opensearch.ubi.model.SettingsConstants;
import org.opensearch.ubi.events.Event;
import org.opensearch.ubi.events.OpenSearchEventManager;
import org.opensearch.ubi.utils.UbiUtils;

import java.util.*;

import static org.opensearch.rest.RestRequest.Method.*;

public class UserBehaviorInsightsRestHandler extends BaseRestHandler {

    private static final Logger LOGGER = LogManager.getLogger(UserBehaviorInsightsRestHandler.class);

    private static final String EVENTS_MAPPING_FILE = "/events-mapping.json";
    private static final String QUERIES_MAPPING_FILE = "/queries-mapping.json";
    public static final int VERSION = 1;

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
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient nodeClient) {

        final String storeName = restRequest.param("store");

        if (restRequest.method() == PUT) {
            //final String index = request.param("index");
            //final String idField = request.param("id_field");
            return create(nodeClient, storeName);
        } else if(restRequest.method() == DELETE) {
            return delete(nodeClient, storeName);
        } else if(restRequest.method() == POST) {
            return post(nodeClient, storeName, restRequest);
        }

        return get(nodeClient);

    }

    private RestChannelConsumer get(final NodeClient nodeClient) {

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

    private RestChannelConsumer create(final NodeClient nodeClient, final String storeName) {
        return (channel) -> {

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
            final String eventsIndex = UbiUtils.getEventsIndexName(storeName);
            final CreateIndexRequest createEventsIndexRequest = new CreateIndexRequest(eventsIndex)
                    .mapping(UbiUtils.getResourceFile(EVENTS_MAPPING_FILE))
                    .settings(indexSettings);

            nodeClient.admin().indices().create(createEventsIndexRequest, new RestToXContentListener<>(channel));

//            // Create the queries index.
//            final String queriesIndex = getEventsIndexName(storeName);
//            final CreateIndexRequest createQueriesIndexRequest = new CreateIndexRequest(eventsIndex)
//                    .mapping(getResourceFile(EVENTS_MAPPING_FILE))
//                    .settings(indexSettings);
//
//            nodeClient.admin().indices().create(createEventsIndexRequest, new RestToXContentListener<>(channel));

        };
    }

    private RestChannelConsumer post(final NodeClient nodeClient, final String storeName, final RestRequest restRequest) {

        try {

            final String eventJson = restRequest.content().utf8ToString();
            final String eventJsonWithTimestamp = setEventTimestamp(eventJson);

            //backend.persistEvent(storeName, eventJsonWithTimestamp);
            //return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "event received"));

            // Add the event for indexing.
            LOGGER.info("Indexing event into {}", storeName);
            final String eventsIndexName = UbiUtils.getEventsIndexName(storeName);

            //return (channel) -> client.index(indexRequest, new RestToXContentListener<>(channel));
            final Event event = new Event(eventsIndexName, eventJson);
            OpenSearchEventManager.getInstance(nodeClient).add(event);

        } catch (JsonProcessingException ex) {
            LOGGER.error("Unable to get/set timestamp on event.", ex);
            return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, "unable to set event timestamp"));
        }

        return (channel) -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "event received"));

    }

    private RestChannelConsumer delete(final NodeClient nodeClient, final String storeName) {

        return (channel) -> {

            // Delete the events index.
            final DeleteIndexRequest deleteEventsIndexRequest = new DeleteIndexRequest(UbiUtils.getEventsIndexName(storeName));
            nodeClient.admin().indices().delete(deleteEventsIndexRequest, new RestToXContentListener<>(channel));

            // Delete the queries index.
            final DeleteIndexRequest deleteQueriesIndexRequest = new DeleteIndexRequest(UbiUtils.getQueriesIndexName(storeName));
            nodeClient.admin().indices().delete(deleteQueriesIndexRequest, new RestToXContentListener<>(channel));

        };

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

}
