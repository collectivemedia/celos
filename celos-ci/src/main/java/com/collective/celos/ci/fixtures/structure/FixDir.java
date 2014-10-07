package com.collective.celos.ci.fixtures.structure;

import java.util.Collections;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDir implements FixObject {

    private final Map<String, FixObject> content;

    public FixDir(Map<String, FixObject> content) {
        this.content = content;
    }

    public Map<String, FixObject> getChildren() {
        return Collections.unmodifiableMap(content);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

}
