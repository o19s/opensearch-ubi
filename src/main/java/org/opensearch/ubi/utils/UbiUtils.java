/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.utils;

import org.opensearch.common.util.io.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * A utility class used by the plugin.
 */
public class UbiUtils {

    /**
     * This is a static utility class.
     */
    private UbiUtils() {

    }

    /**
     * Gets the formatted name of the queries index.
     * @param storeName The name of the UBI store.
     * @return The formatted name of the queries index.
     */
    public static String getQueriesIndexName(final String storeName) {
        return "." + storeName + "_queries";
    }

    /**
     * Gets the formatted name of the events index.
     * @param storeName The name of the UBI store.
     * @return The formatted name of the events index.
     */
    public static String getEventsIndexName(final String storeName) {
        return "." + storeName + "_events";
    }

    /**
     * Gets the content of a resource file.
     * @param fileName The file name to open and read.
     * @return The content of the given filename.
     */
    public static String getResourceFile(final String fileName) {
        try (InputStream is = UbiUtils.class.getResourceAsStream(fileName)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Streams.copy(is, out);
            return out.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create index with resource [" + fileName + "]", e);
        }
    }

}
