package com.collective.celos.fixtures.deploy;

import com.collective.celos.config.ci.CelosCiContext;
import com.collective.celos.fixtures.AbstractFixturePairWorker;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureDeployWorker extends AbstractFixturePairWorker {

    @Override
    public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {
        FileSystem fileSystem = context.getFileSystem();

        fileSystem.mkdirs(hdfsFile.getParent());
        fileSystem.copyFromLocalFile(new Path(localFile.getAbsolutePath()), hdfsFile);
    }


}
