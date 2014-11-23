package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.config.deploy.CelosCiContext;
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

    private final String path;

    public FixDirFromHdfsCreator(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public FixDir create(CelosCiContext celosCiContext) throws Exception {
        return read(new Path(path), celosCiContext).asDir();
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
