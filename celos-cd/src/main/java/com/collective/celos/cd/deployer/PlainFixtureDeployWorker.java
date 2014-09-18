package com.collective.celos.cd.deployer;

import com.collective.celos.cd.config.CelosCdContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureDeployWorker implements FixtureDeployWorker {

    private CelosCdContext config;

    public PlainFixtureDeployWorker(CelosCdContext config) {
        this.config = config;
    }

    @Override
    public void deploy(File localDir, FileSystem fs) throws Exception {

        java.nio.file.Path pathBase = java.nio.file.Paths.get(localDir.getAbsolutePath());
        List<File> filesToCopy = findFiles(localDir);
        for (File f : filesToCopy) {
            java.nio.file.Path pathAbsolute = java.nio.file.Paths.get(f.getAbsolutePath());
            java.nio.file.Path pathRelative = pathBase.relativize(pathAbsolute);
            Path dst = new Path(config.getHdfsPrefix(), pathRelative.toString());
            fs.mkdirs(dst.getParent());
            fs.copyFromLocalFile(new Path(pathAbsolute.toString()), dst);
        }

    }

    private List<File> findFiles(File file) {
        List<File> result = new ArrayList<>();
        for (File child : file.listFiles()) {
            if (child.isDirectory()) {
                result.addAll(findFiles(child));
            } else {
                result.add(child);
            }
        }
        return result;
    }
}
