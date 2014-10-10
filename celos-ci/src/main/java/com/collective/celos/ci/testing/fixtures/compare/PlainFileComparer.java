package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.outfixture.OutFixFile;
import org.apache.commons.io.IOUtils;

/**
 * Created by akonopko on 10/7/14.
 */
public class PlainFileComparer implements FixObjectComparer<OutFixFile, FixFile> {

    public FixObjectCompareResult compare(OutFixFile expectedDirTree, FixFile actualDirTree) throws Exception {
        if (!IOUtils.contentEquals(expectedDirTree.getContent(), actualDirTree.getContent())) {
            return FixObjectCompareResult.failed("File contents differed");
        }
        return FixObjectCompareResult.success();
    }

}
