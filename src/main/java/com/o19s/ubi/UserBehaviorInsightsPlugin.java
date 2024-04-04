/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi;

import com.o19s.ubi.action.UserBehaviorInsightsActionFilter;
import com.o19s.ubi.data.OpenSearchDataManager;
import com.o19s.ubi.model.HeaderConstants;
import com.o19s.ubi.model.SettingsConstants;
import com.o19s.ubi.rest.UserBehaviorInsightsRestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.support.ActionFilter;
import org.opensearch.client.Client;
import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.node.DiscoveryNodes;
import org.opensearch.cluster.service.ClusterService;
import org.opensearch.common.settings.*;
import org.opensearch.core.common.io.stream.NamedWriteableRegistry;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.env.Environment;
import org.opensearch.env.NodeEnvironment;
import org.opensearch.plugins.ActionPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.repositories.RepositoriesService;
import org.opensearch.rest.RestController;
import org.opensearch.rest.RestHandler;
import org.opensearch.rest.RestHeaderDefinition;
import org.opensearch.script.ScriptService;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.watcher.ResourceWatcherService;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;

/**
 * OpenSearch User Behavior Insights Plugin
 */
public class UserBehaviorInsightsPlugin extends Plugin implements ActionPlugin {

    private static final Logger LOGGER = LogManager.getLogger(UserBehaviorInsightsPlugin.class);

    private ActionFilter userBehaviorLoggingFilter;

    /**
     * A map that caches store settings to avoid round-trip calls to the index.
     */
    public static final Map<String, String> storeSettings = new HashMap<>();

    @Override
    public Collection<RestHeaderDefinition> getRestHeaders() {
        return List.of(
                new RestHeaderDefinition(HeaderConstants.EVENT_STORE_HEADER.getHeader(), false),
                new RestHeaderDefinition(HeaderConstants.QUERY_ID_HEADER.getHeader(), false),
                new RestHeaderDefinition(HeaderConstants.SESSION_ID_HEADER.getHeader(), false),
                new RestHeaderDefinition(HeaderConstants.USER_ID_HEADER.getHeader(), false)
        );
    }

    @Override
    public Collection<String> getTaskHeaders() {
        return List.of(
                HeaderConstants.EVENT_STORE_HEADER.getHeader(),
                HeaderConstants.QUERY_ID_HEADER.getHeader(),
                HeaderConstants.SESSION_ID_HEADER.getHeader(),
                HeaderConstants.USER_ID_HEADER.getHeader()
        );
    }

    @Override
    public List<RestHandler> getRestHandlers(final Settings settings,
                                             final RestController restController,
                                             final ClusterSettings clusterSettings,
                                             final IndexScopedSettings indexScopedSettings,
                                             final SettingsFilter settingsFilter,
                                             final IndexNameExpressionResolver indexNameExpressionResolver,
                                             final Supplier<DiscoveryNodes> nodesInCluster) {

        return singletonList(new UserBehaviorInsightsRestHandler());

    }

    @Override
    public List<Setting<?>> getSettings() {

        final List<Setting<?>> settings = new ArrayList<>();

        settings.add(Setting.intSetting(SettingsConstants.VERSION_SETTING, 1, -1, Integer.MAX_VALUE, Setting.Property.IndexScope));
        settings.add(Setting.simpleString(SettingsConstants.INDEX, "", Setting.Property.IndexScope));
        settings.add(Setting.simpleString(SettingsConstants.KEY_FIELD, "", Setting.Property.IndexScope));

        return settings;

    }

    @Override
    public List<ActionFilter> getActionFilters() {
        return singletonList(userBehaviorLoggingFilter);
    }

    @Override
    public Collection<Object> createComponents(
            Client client,
            ClusterService clusterService,
            ThreadPool threadPool,
            ResourceWatcherService resourceWatcherService,
            ScriptService scriptService,
            NamedXContentRegistry xContentRegistry,
            Environment environment,
            NodeEnvironment nodeEnvironment,
            NamedWriteableRegistry namedWriteableRegistry,
            IndexNameExpressionResolver indexNameExpressionResolver,
            Supplier<RepositoriesService> repositoriesServiceSupplier
    ) {

        // TODO: Allow the parameters of the scheduled tasks to be configurable.

        LOGGER.info("Starting UBI scheduled task to persist events.");
        threadPool.scheduler().scheduleWithFixedDelay(() -> {
            OpenSearchDataManager.getInstance(client).processEvents();
        }, 5000, 2000, TimeUnit.MILLISECONDS);

        LOGGER.info("Starting UBI scheduled task to persist queries.");
        threadPool.scheduler().scheduleWithFixedDelay(() -> {
            OpenSearchDataManager.getInstance(client).processQueries();
        }, 5000, 2000, TimeUnit.MILLISECONDS);

        // Initialize the action filter.
        this.userBehaviorLoggingFilter = new UserBehaviorInsightsActionFilter(client, threadPool);

        return Collections.emptyList();

    }

}
