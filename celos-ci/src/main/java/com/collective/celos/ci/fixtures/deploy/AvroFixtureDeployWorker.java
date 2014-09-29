package com.collective.celos.ci.fixtures.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixturePairWorker;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;

/**
 * Created by akonopko on 9/18/14.
 */
public class AvroFixtureDeployWorker extends AbstractFixturePairWorker {

    @Override
    public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {
    }


    public static void writeJsonISToAvroOS(InputStream input, ByteArrayOutputStream output, Schema schema) throws IOException {
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
