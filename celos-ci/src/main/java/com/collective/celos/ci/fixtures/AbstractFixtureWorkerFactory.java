package com.collective.celos.ci.fixtures;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.testing.TestContext;

/**
 * Created by akonopko on 10/4/14.
 */
public abstract class AbstractFixtureWorkerFactory {

    private final CelosCiContext ciContext;
    private final TestContext testContext;

    public AbstractFixtureWorkerFactory(CelosCiContext ciContext, TestContext testContext) {
        this.ciContext = ciContext;
        this.testContext = testContext;
    }

    public abstract String getWorkerType();
    public abstract DeployFixtureWorker createDeployWorker();
    public abstract ComparatorFixtureWorker createCompareWorker();
}
