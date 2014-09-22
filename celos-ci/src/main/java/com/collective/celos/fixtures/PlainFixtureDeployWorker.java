package com.collective.celos.fixtures;

import com.collective.celos.config.CelosCiContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.util.Map;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureDeployWorker extends AbstractFixtureWorker {

    public PlainFixtureDeployWorker(CelosCiContext context) {
        super(context);
    }

    @Override
    public void process(File inputDir) throws Exception {
        FileSystem fileSystem = getContext().getFileSystem();

        Map<File, Path> filePathMap = getLocalFilesToHdfsMap(inputDir);
        for (Map.Entry<File, Path> localToDst : filePathMap.entrySet()) {
            fileSystem.mkdirs(localToDst.getValue().getParent());
            fileSystem.copyFromLocalFile(new Path(localToDst.getKey().getAbsolutePath()), localToDst.getValue());
        }

    }


}
