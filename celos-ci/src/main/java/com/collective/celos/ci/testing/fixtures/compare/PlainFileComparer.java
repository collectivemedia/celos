package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * Created by akonopko on 10/7/14.
 */
public class PlainFileComparer implements FixtureComparer<FixFile> {

    private final InputStream content;
    private final FixFile file;

    public PlainFileComparer(InputStream content, FixFile file) {
        this.content = content;
        this.file = file;
    }

    public FixObjectCompareResult check(CelosCiContext celosCiContext) throws Exception {
        if (!IOUtils.contentEquals(content, file.getContent())) {
            return FixObjectCompareResult.failed("File contents differed");
        }
        return FixObjectCompareResult.success();
    }

}
