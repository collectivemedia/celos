package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.outfixture.OutFixObject;

/**
 * Created by akonopko on 10/7/14.
 */
public interface FixObjectComparer<K extends OutFixObject, T extends FixObject> {

    public FixObjectCompareResult compare(K expectedDirTree, T actualDirTree) throws Exception;

}
