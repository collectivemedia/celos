package com.collective.celos.ci.fixtures;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.*;

import java.io.InputStream;
import java.util.List;

/**
 * Created by akonopko on 10/7/14.
 */
public class HdfsDataReader extends AbstractFileDataReader {

    private final CelosCiContext context;
    private final String path;
    private final boolean sandboxed;

    public HdfsDataReader(CelosCiContext context, String path, boolean sandboxed) {
        this.context = context;
        this.path = path;
        this.sandboxed = sandboxed;
    }

    public List<FileInfo> read() throws Exception {

        RemoteIterator<LocatedFileStatus> iterator = context.getFileSystem().listFiles(new Path(path), true);
        List<FileInfo> result = Lists.newArrayList();
        while (iterator.hasNext()) {
            LocatedFileStatus fileStatus = iterator.next();
            if (!fileStatus.isDirectory()) {
                InputStream is = context.getFileSystem().open(fileStatus.getPath());
                String fullPath = fileStatus.getPath().toString();
                String relativePath = sandboxed ? removeHdfsPrefix(fullPath) : fullPath;
                result.add(new FileInfo(is, fullPath, relativePath));
            }
        }
        return result;
    }

    private String removeHdfsPrefix(String path) {
        if (!path.startsWith(context.getHdfsPrefix())) {
            throw new IllegalStateException("Relative HDFS must start with a prefix: " + context.getHdfsPrefix());
        }
        return StringUtils.removeStart(path, context.getHdfsPrefix());
    }
}
