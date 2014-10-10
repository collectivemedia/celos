package com.collective.celos.ci.testing.fixtures.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.tree.AbstractTreeObjectProcessor;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Created by akonopko on 10/7/14.
 */
public class HdfsFixtureDeployerFileProcessor extends AbstractTreeObjectProcessor<FixObject> {

    private final CelosCiContext context;

    public HdfsFixtureDeployerFileProcessor(CelosCiContext ciContext) {
        this.context = ciContext;
    }

    @Override
    public void process(Path path, FixObject fo) throws IOException {
        if (!fo.isFile()) {
            return;
        }
        FixFile file = (FixFile) fo;
        FileSystem fileSystem = context.getFileSystem();

        org.apache.hadoop.fs.Path writeTo = new org.apache.hadoop.fs.Path(context.getHdfsPrefix() + path.toString());
        fileSystem.mkdirs(writeTo);
        try (
                FSDataOutputStream outputStream = fileSystem.create(writeTo);
                InputStream inputStream = file.getContent();
        ) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

}
