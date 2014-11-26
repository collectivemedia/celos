package com.collective.celos.ci.testing.fixtures.deploy;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.tree.AbstractTreeObjectProcessor;
import com.collective.celos.ci.testing.structure.tree.TreeStructureProcessorRunner;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.Map;

/**
 * Created by akonopko on 21.11.14.
 */
public class HdfsInputDeployer implements FixtureDeployer {

    private final FixObjectCreator<FixObject> fixObjectCreator;
    private final String path;

    public HdfsInputDeployer(FixObjectCreator<FixObject> fixObjectCreator, String path) {
        this.fixObjectCreator = fixObjectCreator;
        this.path = path;
    }

    public void deploy(TestRun testRun) throws Exception {
        FileSystem fileSystem = testRun.getCiContext().getFileSystem();

        PathToFixFileProcessor pathToFile = new PathToFixFileProcessor();
        TreeStructureProcessorRunner.process(fixObjectCreator.create(testRun), pathToFile);

        Path pathPrefixed = new Path(testRun.getHdfsPrefix(), path);
        for (java.nio.file.Path childPath: pathToFile.pathToFiles.keySet()) {
            Path pathTo = new Path(pathPrefixed, childPath.toString());
            fileSystem.mkdirs(pathTo.getParent());

            FSDataOutputStream outputStream = fileSystem.create(pathTo);
            try {
                IOUtils.copy(pathToFile.pathToFiles.get(childPath).getContent(), outputStream);
            } finally {
                outputStream.flush();
                outputStream.close();
            }

        }
    }

    public FixObjectCreator<FixObject> getFixObjectCreator() {
        return fixObjectCreator;
    }

    public String getPath() {
        return path;
    }

    private static class PathToFixFileProcessor extends AbstractTreeObjectProcessor<FixObject> {

        private final Map<java.nio.file.Path, FixFile> pathToFiles = Maps.newHashMap();

        @Override
        public void process(java.nio.file.Path path, FixObject fo) throws IOException {
            if (fo.isFile()) {
                pathToFiles.put(path, fo.asFile());
            }
        }
    }

}
