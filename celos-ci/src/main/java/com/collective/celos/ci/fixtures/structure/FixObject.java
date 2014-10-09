package com.collective.celos.ci.fixtures.structure;

import com.collective.celos.ci.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.fixtures.compare.FixObjectComparer;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class FixObject {

    private final FixObjectComparer comparer;

    protected FixObject(FixObjectComparer comparer) {
        this.comparer = comparer;
    }

    public abstract boolean isDirectory();

    public FixObjectCompareResult compare(FixObject other) throws Exception {
        return comparer.compare(this, other);
    }

    public FixFile asFixFile() {
        return (FixFile) this;
    }

    public FixDir asFixDir() {
        return (FixDir) this;
    }

    public FixObjectComparer getComparer() {
        return comparer;
    }
}
