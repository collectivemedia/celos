package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.mode.test.TestRun;

/**
 * Created by akonopko on 10/10/14.
 */
public interface FixtureComparer {

    public FixObjectCompareResult check(TestRun testRun) throws Exception;

}
