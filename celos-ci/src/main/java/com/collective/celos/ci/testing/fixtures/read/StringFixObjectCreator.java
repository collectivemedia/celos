package com.collective.celos.ci.testing.fixtures.read;

import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.apache.commons.io.IOUtils;

/**
 * Created by akonopko on 10/7/14.
 */
public class StringFixObjectCreator extends AbstractFixObjectCreator<FixFile> {

    private final String content;

    public StringFixObjectCreator(String content) {
        this.content = content;
    }


    public FixFile create() throws Exception {
        return new FixFile(IOUtils.toInputStream(content));
    }
}
