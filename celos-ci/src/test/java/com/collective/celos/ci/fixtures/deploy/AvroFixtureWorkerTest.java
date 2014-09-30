package com.collective.celos.ci.fixtures.deploy;

import com.collective.celos.ci.fixtures.util.AvroJsonConverter;
import org.apache.avro.Schema;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by akonopko on 9/18/14.
 */
public class AvroFixtureWorkerTest {


    @Test
    public void testOne() throws IOException {
        AvroFixtureDeployWorker worker = new AvroFixtureDeployWorker();

        Schema schema = new Schema.Parser().parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/testing/compare/avro/avro.schema"));

        InputStream inputJson = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/testing/compare/avro/avro.json");

        ByteArrayOutputStream outputJson = new ByteArrayOutputStream();

        AvroJsonConverter helper = new AvroJsonConverter();
        helper.writeJsonISToAvroOS(inputJson, outputJson, schema);

        InputStream jsonData = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/testing/compare/avro/avro.json");
        InputStream jsonIsBack = helper.readAvroISToJsonIS(new SeekableByteArrayInput(outputJson.toByteArray()));

        Assert.assertTrue(IOUtils.contentEquals(jsonData, jsonIsBack));
    }
}
