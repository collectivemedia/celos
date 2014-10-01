package com.collective.celos.ci.fixture;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.compare.CelosResultsCompareException;
import com.collective.celos.ci.fixtures.compare.PlainFixtureComparatorWorker;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureComparatorWorkerTest {

    @Test
    public void testProcessPairEqual() throws Exception {
        PlainFixtureComparatorWorker worker = new PlainFixtureComparatorWorker();
        CelosCiContext context = mock(CelosCiContext.class);

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();

        File localFile = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/compare/equal1").toURI());
        Path hdfsFile  = new Path(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/compare/equal2").toURI());
        worker.processPair(context, localFile, hdfsFile);
    }

    @Test(expected = CelosResultsCompareException.class)
    public void testProcessPairNotEqual() throws Exception {
        PlainFixtureComparatorWorker worker = new PlainFixtureComparatorWorker();
        CelosCiContext context = mock(CelosCiContext.class);

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();

        File localFile = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/compare/equal1").toURI());
        Path hdfsFile  = new Path(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/compare/not-equal").toURI());
        worker.processPair(context, localFile, hdfsFile);
    }

    @Test(expected = IllegalStateException.class)
    public void testProcessPairNoLocalFile() throws Exception {
        PlainFixtureComparatorWorker worker = new PlainFixtureComparatorWorker();
        CelosCiContext context = mock(CelosCiContext.class);

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();

        File localFile = new File("blah");
        Path hdfsFile  = new Path(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/compare/not-equal").toURI());
        worker.processPair(context, localFile, hdfsFile);
    }

    @Test(expected = IllegalStateException.class)
    public void testProcessPairNotHdfsFile() throws Exception {
        PlainFixtureComparatorWorker worker = new PlainFixtureComparatorWorker();
        CelosCiContext context = mock(CelosCiContext.class);

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();

        File localFile = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/compare/equal1").toURI());
        Path hdfsFile  = new Path("blah");
        worker.processPair(context, localFile, hdfsFile);
    }


}
