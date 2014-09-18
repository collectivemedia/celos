package com.collective.celos.cd.fixtures;

import com.collective.celos.cd.config.CelosCdContext;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 9/18/14.
 */
public abstract class AbstractFixtureWorker {

    private CelosCdContext context;

    public AbstractFixtureWorker(CelosCdContext context) {
        this.context = context;
    }

    public abstract void process(File localDir) throws Exception;

    public CelosCdContext getContext() {
        return context;
    }

    protected Map<File, Path> getLocalFilesToHdfsMap(File localDir) {
        Map<File, Path> result = new HashMap<>();
        java.nio.file.Path pathBase = java.nio.file.Paths.get(localDir.getAbsolutePath());
        List<File> filesToCopy = findFiles(localDir);
        for (File f : filesToCopy) {
            java.nio.file.Path pathAbsolute = java.nio.file.Paths.get(f.getAbsolutePath());
            java.nio.file.Path pathRelative = pathBase.relativize(pathAbsolute);
            Path dst = new Path(context.getHdfsPrefix(), pathRelative.toString());
            result.put(f, dst);
        }
        return result;
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
