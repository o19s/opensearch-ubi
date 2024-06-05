/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

import org.opensearch.common.settings.Setting;

import java.util.List;

/**
 * The UBI settings.
 */
public class UbiSettings {

    /**
     * The name of the Data Prepper http_source URL for receiving queries.
     */
    public static final String DATA_PREPPER_URL = "ubi.dataprepper.url";

    /**
     * The optional username for Data Prepper's http_source.
     */
    public static final String DATA_PREPPER_AUTH_USERNAME = "ubi.dataprepper.auth.username";

    /**
     * The optional password for Data Prepper's http_source.
     */
    public static final String DATA_PREPPER_AUTH_PASSWORD = "ubi.dataprepper.auth.password";

    private static final Setting<String> DATA_PREPPER_URL_SETTING = Setting.simpleString(
            DATA_PREPPER_URL,
            Setting.Property.Dynamic,
            Setting.Property.NodeScope);

    private static final Setting<String> DATA_PREPPER_AUTH_USERNAME_SETTING = Setting.simpleString(
            DATA_PREPPER_AUTH_USERNAME,
            Setting.Property.Dynamic,
            Setting.Property.NodeScope);

    private static final Setting<String> DATA_PREPPER_AUTH_PASSWORD_PASSWORD = Setting.simpleString(
            DATA_PREPPER_AUTH_PASSWORD,
            Setting.Property.Dynamic,
            Setting.Property.NodeScope);

    /**
     * Gets a list of the UBI plugin settings.
     * @return A list of the UBI plugin settings.
     */
    public static List<Setting<?>> getSettings() {
        return List.of(
                DATA_PREPPER_URL_SETTING,
                DATA_PREPPER_AUTH_USERNAME_SETTING,
                DATA_PREPPER_AUTH_PASSWORD_PASSWORD
        );
    }

}
