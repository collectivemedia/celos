/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.ci.testing.fixtures.convert;

import com.collective.celos.ci.Utils;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.PlainFileComparer;
import com.collective.celos.ci.testing.fixtures.convert.AvroToJsonConverter;
import com.collective.celos.ci.testing.fixtures.convert.JsonToAvroConverter;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 9/29/14.
 */
public class AvroToJsonConverterTest {


    private TestRun testRun;

    @Before
    public void init() {
        testRun = mock(TestRun.class);
    }


    @Test
    public void testEmptyFileConvert() throws IOException {
        AvroToJsonConverter avroToJsonConverter = new AvroToJsonConverter();
        avroToJsonConverter.convert(testRun, new FixFile(new ByteArrayInputStream(new byte[0])));
    }


    @Test
    public void testJsonToAvroEmpty() throws Exception {

        FixFile ff = new FixFile(new ByteArrayInputStream(new byte[0]));

        FixObjectCreator<FixFile> schemaCreator = Utils.wrap(new FixFile(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/avro/avro.schema")));

        JsonToAvroConverter jsonToAvroConverter = new JsonToAvroConverter(schemaCreator);
        FixFile fixFile = jsonToAvroConverter.convert(testRun, ff);
    }


    @Test
    public void testAvroToJsonEmpty() throws Exception {

        FixFile ff = new FixFile(new ByteArrayInputStream(new byte[0]));

        AvroToJsonConverter jsonToAvroConverter = new AvroToJsonConverter();
        FixFile fixFile = jsonToAvroConverter.convert(testRun, ff);
    }

    @Test
    public void testAvroToJsonAndBackConverter() throws Exception {

        FixFile ff = getJsonFixFile();

        FixObjectCreator<FixFile> schemaCreator = Utils.wrap(new FixFile(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/avro/avro.schema")));

        JsonToAvroConverter jsonToAvroConverter = new JsonToAvroConverter(schemaCreator);
        FixFile fixFile = jsonToAvroConverter.convert(testRun, ff);

        AvroToJsonConverter avroToJsonConverter = new AvroToJsonConverter();
        FixFile jsonFixFile = avroToJsonConverter.convert(testRun, fixFile);

        FixObjectCompareResult result = new PlainFileComparer(jsonFixFile.getContent(), getJsonFixFile()).check(null);
        Assert.assertEquals(result.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    private FixFile getJsonFixFile() {
        InputStream jsonData = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/avro/avro.json");
        return new FixFile(jsonData);
    }
}
