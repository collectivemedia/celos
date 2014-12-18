package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.mode.test.TestRun;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class AbstractFixFileConverter {

    public abstract FixFile convert(TestRun tr, FixFile ff) throws Exception;

}
