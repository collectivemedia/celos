package com.collective.celos.ci.testing.fixtures.deploy;

import com.collective.celos.ci.mode.test.TestRun;

/**
 * Created by akonopko on 22.11.14.
 */
public interface FixtureDeployer {

    void deploy(TestRun testRun) throws Exception;

}
