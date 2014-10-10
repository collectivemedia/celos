package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.testing.structure.tree.TreeObject;

/**
 * Created by akonopko on 10/7/14.
 */
public interface FixObject<T extends FixObject> extends TreeObject<T> {

    boolean isFile();

}
