package com.collective.celos.ci.testing.fixtures.read;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
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
public class HdfsTreeFixObjectCreator implements FixObjectCreator {

    private final CelosCiContext context;
    private final String path;
    private final FixObjectComparer<FixDir> dirComparer;
    private final FixObjectComparer<FixFile> fileComparer;

    public HdfsTreeFixObjectCreator(CelosCiContext context, String path) {
        this(context, path, null, null);
    }

    public HdfsTreeFixObjectCreator(CelosCiContext context, String path, FixObjectComparer<FixDir> dirComparer, FixObjectComparer<FixFile> fileComparer) {
        this.context = context;
        this.path = path;
        this.dirComparer = dirComparer == null ? DEFAULT_DIR_COMPARER : dirComparer;
        this.fileComparer = fileComparer == null ? DEFAULT_FILE_COMPARER : fileComparer;
    }

    @Override
    public FixObject create() throws Exception {
        return read(new Path(path));
    }

    private FixObject read(Path path) throws Exception {
        FileStatus fileStatus = context.getFileSystem().getFileStatus(path);
        if (fileStatus.isDirectory()) {
            Map<String, FixObject> content = Maps.newHashMap();
            FileStatus[] statuses = context.getFileSystem().listStatus(fileStatus.getPath());
            for (int i=0; i < statuses.length; i++) {
                FileStatus childStatus = statuses[i];
                content.put(childStatus.getPath().getName(), read(childStatus.getPath()));
            }
            return new FixDir(content, dirComparer);
        } else {
            return new FixFile(context.getFileSystem().open(fileStatus.getPath()), fileComparer);
        }
    }

}
