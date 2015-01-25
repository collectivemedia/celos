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
