package com.collective.celos.ci.testing.structure.fixobject;

import java.util.Collections;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDir extends FixObject {

    private final Map<String, FixObject> content;

    public FixDir(Map<String, FixObject> content) {
        this.content = content;
    }

    public Map<String, FixObject> getChildren() {
        return Collections.unmodifiableMap(content);
    }

    @Override
    public boolean isFile() {
        return false;
    }
}
