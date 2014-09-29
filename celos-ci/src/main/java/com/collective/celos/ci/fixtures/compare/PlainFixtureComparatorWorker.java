package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.AbstractFixturePairWorker;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureComparatorWorker extends AbstractFixturePairWorker {

    @Override
    public void processPair(CelosCiContext context, File localFile, Path hdfsFile) throws Exception {

        System.out.println("Comparing " + localFile.toURI() + " " + hdfsFile.toUri());

        InputStream localIS = new FileInputStream(localFile);
        FSDataInputStream resultIS = context.getFileSystem().open(hdfsFile);
        if (!IOUtil.contentEquals(localIS, resultIS)) {
            throw new CelosResultsCompareException("Expected file "
                    + localFile.getAbsolutePath() + " differs from output");
        }
    }

}
