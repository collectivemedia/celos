package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.apache.commons.io.IOUtils;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixFileFromStringCreator implements FixObjectCreator<FixFile> {

    private final String content;

    public FixFileFromStringCreator(String content) {
        this.content = content;
    }

    public FixFile create(TestRun testRun) throws Exception {
        return new FixFile(IOUtils.toInputStream(content));
    }

    @Override
    public String getDescription(TestRun testRun) {
        return "String fixture";
    }
}
