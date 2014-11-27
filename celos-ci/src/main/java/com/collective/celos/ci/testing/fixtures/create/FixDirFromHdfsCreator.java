package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Maps;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirFromHdfsCreator implements FixObjectCreator<FixDir> {

    private final Path path;

    public FixDirFromHdfsCreator(String path) {
        this(new Path(path));
    }


    public FixDirFromHdfsCreator(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public FixDir create(TestRun testRun) throws Exception {
        Path fullPath = new Path(testRun.getCiContext().getHdfsPrefix(), path);
        return read(fullPath, testRun.getCiContext()).asDir();
    }

    private FixObject read(Path path, CelosCiContext context) throws Exception {
        FileStatus fileStatus = context.getFileSystem().getFileStatus(path);
        if (fileStatus.isDirectory()) {
            Map<String, FixObject> content = Maps.newHashMap();
            FileStatus[] statuses = context.getFileSystem().listStatus(fileStatus.getPath());
            for (int i=0; i < statuses.length; i++) {
                FileStatus childStatus = statuses[i];
                FixObject fixObject = read(childStatus.getPath(), context);
                content.put(childStatus.getPath().getName(), fixObject);
            }
            return new FixDir(content);
        } else {
            return new FixFile(context.getFileSystem().open(fileStatus.getPath()));
        }
    }

}
