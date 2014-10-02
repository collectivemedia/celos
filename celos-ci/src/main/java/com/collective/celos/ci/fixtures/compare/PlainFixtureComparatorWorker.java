package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixtureFileWorker;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureComparatorWorker extends AbstractFixtureFileWorker {

    @Override
    public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {

        System.out.println("Comparing " + localFile.getAbsolutePath() + ", " + hdfsFile.toUri());

        if (!localFile.exists()) {
            throw new IllegalStateException("File " + localFile.getAbsolutePath() + " is absent on local FS");
        }
        if (!context.getFileSystem().exists(hdfsFile)) {
            throw new IllegalStateException("File " + hdfsFile.toUri() + " is absent on HDFS");
        }

        InputStream localIS = new FileInputStream(localFile);
        FSDataInputStream resultIS = context.getFileSystem().open(hdfsFile);
        if (!IOUtil.contentEquals(localIS, resultIS)) {
            throw new CelosResultsCompareException("Expected file "
                    + localFile.getAbsolutePath() + " differs from " + hdfsFile.toUri());
        }
    }

}
