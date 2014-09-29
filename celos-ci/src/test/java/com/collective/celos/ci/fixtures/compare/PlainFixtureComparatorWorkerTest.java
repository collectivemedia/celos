package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.codehaus.plexus.util.IOUtil;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureComparatorWorkerTest {

    @Test
    public void testFixturesAreEqual() throws Exception {
        PlainFixtureComparatorWorker worker = new PlainFixtureComparatorWorker();
        URI file1Uri = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/compare/plain/file-1").toURI();
        File file1 = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/compare/plain/file-1").toURI());

        CelosCiContext context = Mockito.mock(CelosCiContext.class);
        Mockito.doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();

        worker.processPair(context, file1, new Path(file1Uri));
    }

    @Test(expected = CelosResultsCompareException.class)
    public void testFixturesAreNotEqual() throws Exception {
        PlainFixtureComparatorWorker worker = new PlainFixtureComparatorWorker();
        URI file1Uri = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/compare/plain/file-1").toURI();
        File file1 = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/compare/plain/file-2").toURI());

        CelosCiContext context = Mockito.mock(CelosCiContext.class);
        Mockito.doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();

        worker.processPair(context, file1, new Path(file1Uri));
    }

}
