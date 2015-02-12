package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.testing.structure.tree.TreeObject;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class FixFsObject extends FixObject implements TreeObject<FixFsObject> {

    public abstract boolean isFile();

    public boolean isDir() {
        return !isFile();
    }

    public FixFile asFile() {
        return (FixFile) this;
    }

    public FixDir asDir() {
        return (FixDir) this;
    }

}
