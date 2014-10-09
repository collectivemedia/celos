package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.apache.commons.io.IOUtils;

/**
 * Created by akonopko on 10/7/14.
 */
public class PlainFileComparer implements FixObjectComparer<FixFile> {

    public FixObjectCompareResult compare(FixFile expectedDirTree, FixFile actualDirTree) throws Exception {
        if (!IOUtils.contentEquals(expectedDirTree.getContent(), actualDirTree.getContent())) {
            return FixObjectCompareResult.failed("File contents differed");
        }
        return FixObjectCompareResult.success();
    }

}
