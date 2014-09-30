package com.collective.celos.ci.fixtures.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixtureDirWorker;
import com.collective.celos.ci.fixtures.util.AvroJsonConverter;
import org.apache.avro.Schema;
import org.apache.hadoop.fs.*;

import java.io.*;

/**
 * Created by akonopko on 9/18/14.
 */
public class AvroFixtureDeployWorker extends AbstractFixtureDirWorker {

    private static final String AVRO_FILE = "avro.json";
    private static final String SCHEMA_FILE = "avro.schema";

    private AvroJsonConverter avroJsonConverter;

    public AvroFixtureDeployWorker() {
        this.avroJsonConverter = new AvroJsonConverter();
    }

    @Override
    public void processPair(CelosCiContext context, File localDir, Path hdfsUri) throws Exception {
        File jsonFile = new File(localDir, AVRO_FILE);
        File schemaFile = new File(localDir, SCHEMA_FILE);

        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(schemaFile);

        FileSystem fileSystem = context.getFileSystem();
        fileSystem.mkdirs(hdfsUri.getParent());

        avroJsonConverter.writeJsonISToAvroOS(new FileInputStream(jsonFile), fileSystem.create(hdfsUri), schema);
    }


}
