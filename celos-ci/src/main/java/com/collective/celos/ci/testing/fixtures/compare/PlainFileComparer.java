package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * Created by akonopko on 10/7/14.
 */
public class PlainFileComparer implements Comparer<FixFile> {

    private final InputStream content;

    public PlainFileComparer(InputStream content) {
        this.content = content;
    }

    public FixObjectCompareResult check(FixFile file) throws Exception {
        if (!IOUtils.contentEquals(content, file.getContent())) {
            return FixObjectCompareResult.failed("File contents differed");
        }
        return FixObjectCompareResult.success();
    }

}
