package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.config.deploy.CelosCiContext;
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

    public FixFile create(CelosCiContext context) throws Exception {
        return new FixFile(IOUtils.toInputStream(content));
    }
}
