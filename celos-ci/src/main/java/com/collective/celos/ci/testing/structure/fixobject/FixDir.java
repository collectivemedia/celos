package com.collective.celos.ci.testing.structure.fixobject;

import java.util.Collections;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDir extends FixFsObject {

    private final Map<String, FixFsObject> content;

    public FixDir(Map<String, FixFsObject> content) {
        this.content = content;
    }

    public Map<String, FixFsObject> getChildren() {
        return Collections.unmodifiableMap(content);
    }

    @Override
    public boolean isFile() {
        return false;
    }
}
