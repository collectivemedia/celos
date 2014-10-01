package com.collective.celos.ci.fixtures;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akonopko on 9/18/14.
 */
public abstract class AbstractFixtureFileWorker {

    public abstract void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception;

    public void process(CelosCiContext context, File localDir) throws Exception {

        java.nio.file.Path pathBase = java.nio.file.Paths.get(localDir.getAbsolutePath());
        List<File> filesToCopy = findFiles(localDir);
        for (File f : filesToCopy) {
            java.nio.file.Path pathAbsolute = java.nio.file.Paths.get(f.getAbsolutePath());
            java.nio.file.Path pathRelative = pathBase.relativize(pathAbsolute);
            Path dst = new Path(context.getHdfsPrefix(), pathRelative.toString());
            processPair(context, f, dst);
        }
    }

    protected List<File> findFiles(File file) {
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
