package com.collective.celos.ci.fixtures;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.InputStream;
import java.util.List;

/**
 * Created by akonopko on 10/7/14.
 */
public class HdfsDeployer extends AbstractFileDeployer {

    private final CelosCiContext context;

    public HdfsDeployer(CelosCiContext context, List<AbstractFileDataReader> readers) {
        super(readers);
        this.context = context;
    }

    public void deploy() throws Exception {
        for (AbstractFileDataReader profisioner : getReaders()) {
            profisioner.read();
            List<FileInfo> files = profisioner.read();
            for (FileInfo fi : files) {
                FileSystem fileSystem = context.getFileSystem();

                Path writeTo = new Path(context.getHdfsPrefix(), fi.getRelativePath());
                fileSystem.mkdirs(writeTo);
                try (
                        FSDataOutputStream outputStream = fileSystem.create(writeTo);
                        InputStream inputStream = fi.getInputStream()
                ) {
                    IOUtils.copy(inputStream, outputStream);
                }
            }
        }
    }
}
