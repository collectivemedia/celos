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
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.AbstractFixObjectConverter;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;

import java.io.*;

/**
 * Created by akonopko on 9/29/14.
 */
public class JsonToAvroConverter extends AbstractFixObjectConverter<FixFile, FixFile> {

    private final FixObjectCreator<FixFile> schemaCreator;

    public JsonToAvroConverter(FixObjectCreator<FixFile> schemaCreator) {
        this.schemaCreator = schemaCreator;
    }

    @Override
    public FixFile convert(TestRun tr, FixFile ff) throws Exception {
        Schema schema = new Schema.Parser().parse(schemaCreator.create(tr).getContent());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream input = ff.getContent();
        DataFileWriter<Object> writer;;
        try {
            DatumReader<Object> reader = new GenericDatumReader<>(schema);
            DataInputStream din = new DataInputStream(input);
            writer = new DataFileWriter<>(new GenericDatumWriter<>());
            writer.create(schema, baos);
            Decoder decoder = DecoderFactory.get().jsonDecoder(schema, din);
            Object datum;
            while (true) {
                try {
                    datum = reader.read(null, decoder);
                } catch (EOFException eofe) {
                    break;
                }
                writer.append(datum);
            }
            writer.flush();
        } finally {
            input.close();
        }
        return new FixFile(new ByteArrayInputStream(baos.toByteArray()));
    }


}
