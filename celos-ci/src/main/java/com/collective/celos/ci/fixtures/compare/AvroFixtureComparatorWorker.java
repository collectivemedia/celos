package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixtureDirWorker;
import com.collective.celos.ci.fixtures.AbstractFixtureFileWorker;
import com.collective.celos.ci.fixtures.util.AvroJsonConverter;
import com.collective.celos.ci.fixtures.util.JsonEntity;
import org.apache.avro.file.SeekableInput;
import org.apache.hadoop.fs.AvroFSInput;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.Path;

import java.io.*;

/**
 * Created by akonopko on 9/18/14.
 */
public class AvroFixtureComparatorWorker extends AbstractFixtureDirWorker {

    private AvroJsonConverter avroJsonConverter;

    public AvroFixtureComparatorWorker() {
        this.avroJsonConverter = new AvroJsonConverter();
    }

    @Override
    public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {
        //TODO:
    }

    void compareStreamsWithJson(InputStream stream1, InputStream stream2, String inputName) throws Exception {
        //TODO:
    }

}
