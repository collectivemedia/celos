package com.collective.celos.ci.fixture;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixtureDirWorker;
import com.collective.celos.ci.fixtures.AbstractFixtureFileWorker;
import com.google.common.collect.Maps;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 9/18/14.
 */
public class AbstractFixtureDirWorkerTest {

    @Test
    public void testProcessLocalDirNoWorker() throws Exception {

        CelosCiContext context = mock(CelosCiContext.class);
        doReturn("$hdfsPrefix$").when(context).getHdfsPrefix();
        File inputFolder = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/input").toURI());

        TraverseFixtureFileWorker worker = new TraverseFixtureFileWorker();
        worker.process(context, inputFolder);
        Map<String, Path> real = Maps.newHashMap();
        for (Map.Entry<File, Path> entry : worker.visited.entrySet()) {
            real.put(getRelativePath(entry.getKey(), inputFolder), entry.getValue());
        }

        Map<String, Path> expected = Maps.newHashMap();

        expected.put("PLAIN/dir2/dir3/dir4", new Path("$hdfsPrefix$/PLAIN/dir2/dir3/dir4"));
        expected.put("Avro/dir2/dir3/dir4", new Path("$hdfsPrefix$/Avro/dir2/dir3/dir4"));
        expected.put("Avro/dir1/dir2", new Path("$hdfsPrefix$/Avro/dir1/dir2"));
        expected.put("plain/dir2/dir3/dir4", new Path("$hdfsPrefix$/plain/dir2/dir3/dir4"));
        expected.put("PLAIN/dir1/dir2", new Path("$hdfsPrefix$/PLAIN/dir1/dir2"));
        expected.put("plain/dir1/dir2", new Path("$hdfsPrefix$/plain/dir1/dir2"));

        Assert.assertEquals(expected, real);
    }


    public String getRelativePath(File file, File base) {
        java.nio.file.Path pathAbsolute = java.nio.file.Paths.get(file.getAbsolutePath());
        java.nio.file.Path pathRelative = java.nio.file.Paths.get(base.getAbsolutePath()).relativize(pathAbsolute);
        return pathRelative.toString();
    }

    public static class TraverseFixtureFileWorker extends AbstractFixtureDirWorker {

        private Map<File, Path> visited = Maps.newHashMap();

        @Override
        public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {
            visited.put(localFile, hdfsFile);
        }
    }

}
