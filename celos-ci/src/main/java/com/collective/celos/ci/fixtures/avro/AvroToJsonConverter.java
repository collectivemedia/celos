package com.collective.celos.ci.fixtures.avro;

import com.collective.celos.ci.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.fixtures.compare.PlainFileComparer;
import com.collective.celos.ci.fixtures.processor.AbstractFixFileConverter;
import com.collective.celos.ci.fixtures.structure.FixFile;
import org.apache.avro.Schema;
import org.apache.avro.file.*;
import org.apache.avro.file.FileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.*;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Created by akonopko on 9/29/14.
 */
public class AvroToJsonConverter extends AbstractFixFileConverter {

    protected AvroToJsonConverter(FixObjectComparer<FixFile> newComparer) {
        super(newComparer);
    }

    @Override
    public FixFile convert(FixFile ff) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        GenericDatumReader<Object> reader = new GenericDatumReader<>();
        byte[] bytes = IOUtils.toByteArray(ff.getContent());
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
        return new FixFile(new ByteArrayInputStream(os.toByteArray()), getNewComparer());
    }
}
