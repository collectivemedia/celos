package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.structure.fixobject.FixObject;

/**
 * Created by akonopko on 10/10/14.
 */
public interface Comparer<T extends FixObject> {

    public FixObjectCompareResult check(T fo) throws Exception;

}
