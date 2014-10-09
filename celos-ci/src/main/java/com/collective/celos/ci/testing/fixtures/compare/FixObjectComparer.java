package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.structure.fixobject.FixObject;

/**
 * Created by akonopko on 10/7/14.
 */
public interface FixObjectComparer<T extends FixObject> {

    public FixObjectCompareResult compare(T expectedDirTree, T actualDirTree) throws Exception;

}
