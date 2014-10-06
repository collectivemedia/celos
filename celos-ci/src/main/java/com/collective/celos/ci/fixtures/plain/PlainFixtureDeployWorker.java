package com.collective.celos.ci.fixtures.plain;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixtureFileWorker;
import com.collective.celos.ci.fixtures.DeployFixtureWorker;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureDeployWorker extends AbstractFixtureFileWorker implements DeployFixtureWorker {

    public final static String WORKER_TYPE = "PLAIN";

    @Override
    public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {
        FileSystem fileSystem = context.getFileSystem();

        if (!localFile.exists()) {
            throw new IllegalStateException("File " + localFile + " is absent on local FS");
        }
        if (fileSystem.exists(hdfsFile)) {
            throw new IllegalStateException("File " + hdfsFile.toUri() + " already exists on HDFS");
        }

        fileSystem.mkdirs(hdfsFile.getParent());
        fileSystem.copyFromLocalFile(new Path(localFile.getAbsolutePath()), hdfsFile);
    }


}
