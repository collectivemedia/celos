package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.fixtures.structure.FixDir;

/**
 * Created by akonopko on 10/7/14.
 */
public interface FixDirComparer {

    public void compare(FixDir expectedDirTree, FixDir actualDirTree) throws Exception;

}
