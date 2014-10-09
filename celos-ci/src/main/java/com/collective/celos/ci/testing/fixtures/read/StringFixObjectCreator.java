package com.collective.celos.ci.testing.fixtures.read;

import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import org.apache.commons.io.IOUtils;

/**
 * Created by akonopko on 10/7/14.
 */
public class StringFixObjectCreator implements FixObjectCreator {

    private final String content;

    public StringFixObjectCreator(String content) {
        this.content = content;
    }

    @Override
    public FixObject create() throws Exception {
        return new FixFile(IOUtils.toInputStream(content), DEFAULT_FILE_COMPARER);
    }
}
