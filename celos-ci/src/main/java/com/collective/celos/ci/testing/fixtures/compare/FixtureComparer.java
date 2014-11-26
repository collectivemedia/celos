package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;

/**
 * Created by akonopko on 10/10/14.
 */
public interface FixtureComparer {

    public FixObjectCompareResult check(TestRun testRun) throws Exception;

}
