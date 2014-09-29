package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixturePairWorker;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.FileReader;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.hadoop.fs.AvroFSInput;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.Path;

import java.io.*;

/**
 * Created by akonopko on 9/18/14.
 */
public class AvroFixtureComparatorWorker extends AbstractFixturePairWorker {

    @Override
    public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {

        SeekableInput input = new AvroFSInput(FileContext.getFileContext(context.getConfiguration()), hdfsFile);
        InputStream stream1 = readInputAvroToJson(input);
        InputStream stream2 = new FileInputStream(localFile);
        String inputName = localFile.getAbsolutePath();

        compareStreamsWithJson(stream1, stream2, inputName);
    }

    void compareStreamsWithJson(InputStream stream1, InputStream stream2, String inputName) throws Exception {
        BufferedReader jsonFromHdfs = new BufferedReader(new InputStreamReader(stream1));
        BufferedReader jsonFromLocal = new BufferedReader(new InputStreamReader(stream2));

        JsonIgnorePathsComparator comparator = new JsonIgnorePathsComparator();

        Exception e = new CelosResultsCompareException("Expected file " + inputName + " differs from output");

        String hdfsLine;
        while ((hdfsLine = jsonFromHdfs.readLine()) != null) {
            String localLine = jsonFromLocal.readLine();

            if (localLine == null) {
                throw e;
            }

            if (!comparator.compare(localLine, hdfsLine)) {
                throw e;
            }
        }
        if (jsonFromLocal.readLine() != null) {
            throw e;
        }
    }

    InputStream readInputAvroToJson(SeekableInput input) throws IOException {

        ByteOutputStream stream1 = new ByteOutputStream();

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
        return new ByteArrayInputStream(stream1.getBytes());
    }

}
