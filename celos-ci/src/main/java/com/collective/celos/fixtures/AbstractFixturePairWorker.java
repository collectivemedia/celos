package com.collective.celos.fixtures;

import com.collective.celos.config.CelosCiContext;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 9/18/14.
 */
public abstract class AbstractFixturePairWorker {

    private CelosCiContext context;

    public AbstractFixturePairWorker(CelosCiContext context) {
        this.context = context;
    }

    public void process(File localDir) throws Exception {

        Map<File, Path> filePathMap = getLocalFilesToHdfsMap(localDir);
        for (Map.Entry<File, Path> localToDst : filePathMap.entrySet()) {
            processPair(localToDst.getKey(), localToDst.getValue());
        }

    }

    public abstract void processPair(File localFile, Path hdfsFile) throws Exception;

    public CelosCiContext getContext() {
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
