package com.collective.celos.ci.fixtures.read;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.structure.FixDir;
import com.collective.celos.ci.fixtures.structure.FixFile;
import com.collective.celos.ci.fixtures.structure.FixObject;
import com.google.common.collect.Maps;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class HdfsDataReader implements FileDataReader {

    private final CelosCiContext context;
    private final String path;

    public HdfsDataReader(CelosCiContext context, String path, boolean sandboxed) {
        this.context = context;
        this.path = path;
    }

    @Override
    public FixObject read() throws Exception {
        return read(context.getFileSystem().getFileStatus(new Path(path)));
    }

    private FixObject read(FileStatus fileStatus) throws Exception {
        if (fileStatus.isDirectory()) {
            Map<String, FixObject> content = Maps.newHashMap();
            RemoteIterator<LocatedFileStatus> iterator = context.getFileSystem().listFiles(fileStatus.getPath(), false);
            while (iterator.hasNext()) {
                LocatedFileStatus childStatus = iterator.next();
                content.put(childStatus.getPath().getName(), read(childStatus));
            }
            return new FixDir(content);
        } else {
            return new FixFile(context.getFileSystem().open(fileStatus.getPath()));
        }
    }

}
