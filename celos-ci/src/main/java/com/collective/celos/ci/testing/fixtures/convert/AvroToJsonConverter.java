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

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.AbstractFixObjectConverter;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.FileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by akonopko on 9/29/14.
 */
public class AvroToJsonConverter extends AbstractFixObjectConverter<FixFile, FixFile> {

    @Override
    public FixFile convert(TestRun testRun, FixFile ff) throws IOException {
        byte[] bytes = IOUtils.toByteArray(ff.getContent());
        if (bytes.length == 0) {
            return ff;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GenericDatumReader<Object> reader = new GenericDatumReader<>();
        FileReader<Object> fileReader =  DataFileReader.openReader(new SeekableByteArrayInput(bytes), reader);
        try {
            Schema schema = fileReader.getSchema();
            DatumWriter<Object> writer = new GenericDatumWriter<>(schema);
            JsonEncoder encoder = EncoderFactory.get().jsonEncoder(schema, os);

            for (Object datum : fileReader) {
                writer.write(datum, encoder);
            }
            encoder.flush();
        } finally {
            fileReader.close();
        }
        return new FixFile(new ByteArrayInputStream(os.toByteArray()));
    }
}
