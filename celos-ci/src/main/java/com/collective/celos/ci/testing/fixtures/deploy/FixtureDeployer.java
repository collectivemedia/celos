package com.collective.celos.ci.testing.fixtures.deploy;

import com.collective.celos.ci.config.testing.TestContext;

/**
 * Created by akonopko on 22.11.14.
 */
public interface FixtureDeployer {

    void deploy(TestContext testContext) throws Exception;

}
