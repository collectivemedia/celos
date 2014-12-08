package com.collective.celos.ci.testing;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.PlainFileComparer;
import com.collective.celos.ci.testing.fixtures.convert.avro.AvroToJsonConverter;
import com.collective.celos.ci.testing.fixtures.convert.avro.JsonToAvroConverter;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.apache.avro.Schema;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by akonopko on 9/29/14.
 */
public class AvroToJsonConverterTest {

    @Test
    public void testEmptyFileConvert() throws IOException {
        AvroToJsonConverter avroToJsonConverter = new AvroToJsonConverter();
        avroToJsonConverter.convert(new FixFile(new ByteArrayInputStream(new byte[0])));
    }

    @Test
    public void testAvroToJsonAndBackConverter() throws Exception {

        Schema schema = new Schema.Parser().parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/avro/avro.schema"));
        FixFile ff = getJsonFixFile();
        JsonToAvroConverter jsonToAvroConverter = new JsonToAvroConverter(schema);
        FixFile fixFile = jsonToAvroConverter.convert(ff);

        AvroToJsonConverter avroToJsonConverter = new AvroToJsonConverter();
        FixFile jsonFixFile = avroToJsonConverter.convert(fixFile);

        FixObjectCompareResult result = new PlainFileComparer(jsonFixFile.getContent(), getJsonFixFile()).check(null);
        Assert.assertEquals(result.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    private FixFile getJsonFixFile() {
        InputStream jsonData = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/avro/avro.json");
        return new FixFile(jsonData);
    }
}
