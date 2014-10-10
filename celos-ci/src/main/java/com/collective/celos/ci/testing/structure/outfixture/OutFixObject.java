package com.collective.celos.ci.testing.structure.outfixture;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.tree.TreeObject;

/**
 * Created by akonopko on 10/7/14.
 */
public interface OutFixObject<T extends OutFixObject> extends FixObject<T> {

    public FixObjectCompareResult compare(FixObject other) throws Exception;

    public FixObjectComparer getComparer();

}
