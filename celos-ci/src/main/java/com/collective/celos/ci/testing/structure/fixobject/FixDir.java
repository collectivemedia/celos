package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;

import java.util.Collections;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDir<T extends FixObject> implements FixObject<T> {

    private final Map<String, T> content;

    public FixDir(Map<String, T> content) {
        this.content = content;
    }

    public Map<String, T> getChildren() {
        return Collections.unmodifiableMap(content);
    }

    @Override
    public boolean isFile() {
        return false;
    }
}
