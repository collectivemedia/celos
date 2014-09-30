package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixtureFileWorker;
import com.collective.celos.ci.fixtures.util.AvroJsonConverter;
import com.collective.celos.ci.fixtures.util.JsonComparator;
import org.apache.avro.file.SeekableInput;
import org.apache.hadoop.fs.AvroFSInput;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.Path;

import java.io.*;

/**
 * Created by akonopko on 9/18/14.
 */
public class AvroFixtureComparatorWorker extends AbstractFixtureFileWorker {

    private AvroJsonConverter avroJsonConverter;

    public AvroFixtureComparatorWorker() {
        this.avroJsonConverter = new AvroJsonConverter();
    }

    @Override
    public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {

        SeekableInput input = new AvroFSInput(FileContext.getFileContext(context.getConfiguration()), hdfsFile);
        InputStream stream1 = avroJsonConverter.readAvroISToJsonIS(input);
        InputStream stream2 = new FileInputStream(localFile);
        String inputName = localFile.getAbsolutePath();

        compareStreamsWithJson(stream1, stream2, inputName);
    }

    void compareStreamsWithJson(InputStream stream1, InputStream stream2, String inputName) throws Exception {
        BufferedReader jsonFromHdfs = new BufferedReader(new InputStreamReader(stream1));
        BufferedReader jsonFromLocal = new BufferedReader(new InputStreamReader(stream2));

        JsonComparator comparator = new JsonComparator();

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

}
