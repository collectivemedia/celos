package com.collective.celos.ci.fixtures.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixtureFileWorker;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureDeployWorker extends AbstractFixtureFileWorker {

    @Override
    public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {
        FileSystem fileSystem = context.getFileSystem();

        fileSystem.mkdirs(hdfsFile.getParent());
        fileSystem.copyFromLocalFile(new Path(localFile.getAbsolutePath()), hdfsFile);
    }


}
