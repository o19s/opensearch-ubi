package org.opensearch.ubi;

import org.opensearch.test.OpenSearchTestCase;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class UbiEventSchemaTests extends OpenSearchTestCase {

    public void testValidateSchema() throws Exception {

        // TODO: Download appropriate version, e.g. https://raw.githubusercontent.com/o19s/ubi/main/schema/X.Y.Z/event.schema.json

        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("event.schema.json").getFile());

        final String schema = Files.readString(file.toPath(), Charset.defaultCharset());



    }

}
