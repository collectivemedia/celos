package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.testing.structure.tree.TreeObject;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixFile implements FixObject {

    private final InputStream inputStream;

    public FixFile(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getContent() {
        return inputStream;
    }

    @Override
    public Map<String, FixObject> getChildren() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public boolean isFile() {
        return true;
    }
}
