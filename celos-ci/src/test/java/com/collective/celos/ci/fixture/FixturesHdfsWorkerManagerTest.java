package com.collective.celos.ci.fixture;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixtureFileWorker;
import com.collective.celos.ci.fixtures.FixturesHdfsWorkerManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Created by akonopko on 9/18/14.
 */
public class FixturesHdfsWorkerManagerTest {

    @Test
    public void testProcessLocalDir() throws Exception {

        CelosCiContext context = mock(CelosCiContext.class);
        Map<String, AbstractFixtureFileWorker> fixtureWorkers = Maps.newHashMap();
        TraverseFixtureFileWorker worker = new TraverseFixtureFileWorker();
        FixturesHdfsWorkerManager manager = new FixturesHdfsWorkerManager(context);
        manager.addFixtureWorker("plain", worker);
        manager.addFixtureWorker("avro", worker);
        File inputFolder = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/input").toURI());
        manager.processLocalDir(inputFolder);

        Set<String> relativeVisitedDirs = Sets.newHashSet();
        for (File f : worker.visited) {
            relativeVisitedDirs.add(getRelativePath(f, inputFolder));
        }
        Assert.assertEquals(relativeVisitedDirs, Sets.newHashSet("plain", "PLAIN", "Avro"));
    }

    @Test(expected = RuntimeException.class)
    public void testProcessLocalDirNoWorker() throws Exception {

        CelosCiContext context = mock(CelosCiContext.class);
        Map<String, AbstractFixtureFileWorker> fixtureWorkers = Maps.newHashMap();
        TraverseFixtureFileWorker worker = new TraverseFixtureFileWorker();
        FixturesHdfsWorkerManager manager = new FixturesHdfsWorkerManager(context);
        manager.addFixtureWorker("plain", worker);
        File inputFolder = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/input").toURI());
        manager.processLocalDir(inputFolder);

        Set<String> relativeVisitedDirs = Sets.newHashSet();
        for (File f : worker.visited) {
            relativeVisitedDirs.add(getRelativePath(f, inputFolder));
        }
        Assert.assertEquals(relativeVisitedDirs, Sets.newHashSet("plain", "PLAIN", "Avro"));
    }


    public String getRelativePath(File file, File base) {
        java.nio.file.Path pathAbsolute = java.nio.file.Paths.get(file.getAbsolutePath());
        java.nio.file.Path pathRelative = java.nio.file.Paths.get(base.getAbsolutePath()).relativize(pathAbsolute);
        return pathRelative.toString();
    }

    public static class TraverseFixtureFileWorker extends AbstractFixtureFileWorker {

        private List<File> visited = Lists.newArrayList();

        public void process(CelosCiContext context, File localDir) throws Exception {
            visited.add(localDir);
        }

        @Override
        public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {
        }
    }

}
