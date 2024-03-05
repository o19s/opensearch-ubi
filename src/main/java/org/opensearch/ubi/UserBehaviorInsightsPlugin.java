/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

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
import org.opensearch.ubi.action.UserBehaviorInsightsActionFilter;
import org.opensearch.ubi.action.UserBehaviorInsightsRestHandler;
import org.opensearch.ubi.events.OpenSearchEventManager;
import org.opensearch.ubi.model.HeaderConstants;
import org.opensearch.ubi.model.SettingsConstants;
import org.opensearch.watcher.ResourceWatcherService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;

public class UserBehaviorInsightsPlugin extends Plugin implements ActionPlugin {

    private static final Logger LOGGER = LogManager.getLogger(UserBehaviorInsightsPlugin.class);

    private ActionFilter userBehaviorLoggingFilter;

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
        settings.add(Setting.simpleString(SettingsConstants.ID_FIELD, "", Setting.Property.IndexScope));

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

        // TODO Only start this if an OpenSearch store is already initialized.
        // Otherwise, start it when a store is initialized.

        LOGGER.info("Creating UBI scheduled task to persist events.");
        // TODO: Allow these time parameters to be configurable.
        threadPool.scheduler().scheduleAtFixedRate(() -> {
            OpenSearchEventManager.getInstance(client).process();
        }, 0, 2000, TimeUnit.MILLISECONDS);

        // Initialize the action filter.
        this.userBehaviorLoggingFilter = new UserBehaviorInsightsActionFilter(client, threadPool);

        return Collections.emptyList();

    }

}
