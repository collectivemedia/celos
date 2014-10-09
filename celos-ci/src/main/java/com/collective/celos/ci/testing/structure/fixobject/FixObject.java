package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.testing.structure.tree.TreeObject;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class FixObject implements TreeObject<FixObject> {

    private final FixObjectComparer comparer;

    protected FixObject(FixObjectComparer comparer) {
        this.comparer = comparer;
    }

    public FixObjectCompareResult compare(FixObject other) throws Exception {
        return comparer.compare(this, other);
    }

    public abstract boolean isFile();

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
