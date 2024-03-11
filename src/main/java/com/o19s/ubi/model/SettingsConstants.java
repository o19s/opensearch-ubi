/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package com.o19s.ubi.model;

/**
 * Settings constants used by the plugin.
 */
public class SettingsConstants {

    /**
     * The schema version.
     */
    public static final String VERSION_SETTING = "index.ubi.version";

    /**
     * The name of the UBI store.
     */
    public static final String INDEX = "index.ubi.store";

    /**
     * The field in an index's mapping that will be used as the unique identifier for a query result item.
     */
    public static final String ID_FIELD = "index.ubi.id_field";

}
