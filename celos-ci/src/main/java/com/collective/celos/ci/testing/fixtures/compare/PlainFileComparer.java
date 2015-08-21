package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * Created by akonopko on 10/7/14.
 */
public class PlainFileComparer implements FixtureComparer {

    private final InputStream content;
    private final FixFile file;

    public PlainFileComparer(InputStream content, FixFile file) {
        this.content = content;
        this.file = file;
    }

    public FixObjectCompareResult check(TestRun testRun) throws Exception {
        if (!IOUtils.contentEquals(content, file.getContent())) {
            return FixObjectCompareResult.failed("File contents differed");
        }
        return FixObjectCompareResult.SUCCESS;
    }

}
