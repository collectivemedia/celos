package com.collective.celos.fixtures;

import com.collective.celos.config.CelosCiContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureDeployWorker extends AbstractFixturePairWorker {

    public PlainFixtureDeployWorker(CelosCiContext context) {
        super(context);
    }

    @Override
    public void processPair(File localFile, Path hdfsFile) throws Exception {
        FileSystem fileSystem = getContext().getFileSystem();

        fileSystem.mkdirs(hdfsFile.getParent());
        fileSystem.copyFromLocalFile(new Path(localFile.getAbsolutePath()), hdfsFile);
    }


}
