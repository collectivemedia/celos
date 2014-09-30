package com.collective.celos.ci.fixtures.util;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.FileReader;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.*;

import java.io.*;

/**
 * Created by akonopko on 9/29/14.
 */
public class AvroJsonConverter {

    public InputStream readAvroISToJsonIS(SeekableInput input) throws IOException {

        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();

        GenericDatumReader<Object> reader = new GenericDatumReader<>();
        FileReader<Object> fileReader =  DataFileReader.openReader(input, reader);
        try {
            Schema schema = fileReader.getSchema();
            DatumWriter<Object> writer = new GenericDatumWriter<>(schema);
            JsonEncoder encoder = EncoderFactory.get().jsonEncoder(schema, stream1);

            for (Object datum : fileReader) {
                writer.write(datum, encoder);
            }
            encoder.flush();
        } finally {
            fileReader.close();
        }
        return new ByteArrayInputStream(stream1.toByteArray());
    }

    public void writeJsonISToAvroOS(InputStream input, OutputStream output, Schema schema) throws IOException {
        DataFileWriter<Object> writer;;
        try {
            DatumReader<Object> reader = new GenericDatumReader<>(schema);
            DataInputStream din = new DataInputStream(input);
            writer = new DataFileWriter<>(new GenericDatumWriter<>());
            writer.create(schema, output);
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
    }


}
