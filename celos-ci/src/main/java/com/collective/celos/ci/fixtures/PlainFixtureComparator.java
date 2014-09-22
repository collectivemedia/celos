package com.collective.celos.ci.fixtures;

import com.collective.celos.config.CelosCiContext;
import com.collective.celos.fixtures.AbstractFixtureWorker;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureComparator extends AbstractFixtureWorker {

    public PlainFixtureComparator(CelosCiContext context) {
        super(context);
    }

    @Override
    public void process(File localDir) throws Exception {

        Map<File, Path> filePathMap = getLocalFilesToHdfsMap(localDir);
        for (Map.Entry<File, Path> localToDst : filePathMap.entrySet()) {
            comparePair(localToDst.getKey(), localToDst.getValue());
        }

    }

    private void comparePair(File local, Path remote) throws Exception {
        InputStream localIS = new FileInputStream(local);
        FSDataInputStream resultIS = getContext().getFileSystem().open(remote);
        if (!IOUtil.contentEquals(localIS, resultIS)) {
            throw new CelosResultsCompareException("Expected file "
                    + local.getAbsolutePath() + " differs from output");
        }
    }

}
