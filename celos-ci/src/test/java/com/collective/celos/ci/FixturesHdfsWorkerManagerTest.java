package com.collective.celos.ci;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixtureFileWorker;
import com.collective.celos.ci.fixtures.FixturesHdfsWorkerManager;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 9/18/14.
 */
public class FixturesHdfsWorkerManagerTest {

    @Test
    public void testOne() throws Exception {
        CelosCiContext ciContext = mock(CelosCiContext.class);
        Mockito.doReturn("prefix").when(ciContext).getHdfsPrefix();
        MockAbstractFixtureFileWorker travers1 = new MockAbstractFixtureFileWorker();
        MockAbstractFixtureFileWorker travers2 = new MockAbstractFixtureFileWorker();
        Map<String, ? extends AbstractFixtureFileWorker> fixtureWorkers = ImmutableMap.of("plain", travers1, "avro", travers2);
        FixturesHdfsWorkerManager manager = new FixturesHdfsWorkerManager(ciContext, fixtureWorkers);

        File localDir = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/fixtures/worker").toURI());

        manager.processLocalDir(localDir.getAbsolutePath());

        System.out.println(travers1.filesTraversed.keySet());
    }

    static class MockAbstractFixtureFileWorker extends AbstractFixtureFileWorker {

        private Map<String, String> filesTraversed = new HashMap<>();

        @Override
        public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {
            filesTraversed.put(localFile.toURI().toString(), hdfsFile.toUri().toString());
        }

        public Map<String, String> getFilesTraversed() {
            return filesTraversed;
        }
    }

}
