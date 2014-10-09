package com.collective.celos.ci.fixtures.structure;

import com.collective.celos.ci.fixtures.compare.FixObjectComparer;

import java.util.Collections;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDir extends FixObject {

    private final Map<String, FixObject> content;

    public FixDir(Map<String, FixObject> content, FixObjectComparer<FixDir> comparer) {
        super(comparer);
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
