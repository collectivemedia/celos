package com.collective.celos.ci.fixtures;

import com.collective.celos.ci.fixtures.util.AvroJsonConverter;
import org.apache.avro.Schema;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * Created by akonopko on 9/29/14.
 */
public class AvroJsonConverterTest {

    @Test
    public void testAvroHelper() throws IOException, URISyntaxException {

        AvroJsonConverter helper = new AvroJsonConverter();

        Schema schema = new Schema.Parser().parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/testing/compare/avro/avro.schema"));

        InputStream inputJson = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/testing/compare/avro/avro.json");

        ByteArrayOutputStream outputJson = new ByteArrayOutputStream();

        helper.writeJsonISToAvroOS(inputJson, outputJson, schema);

        InputStream jsonData = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/testing/compare/avro/avro.json");
        InputStream jsonIsBack = helper.readAvroISToJsonIS(new SeekableByteArrayInput(outputJson.toByteArray()));

        Assert.assertTrue(IOUtils.contentEquals(jsonData, jsonIsBack));
    }
}
