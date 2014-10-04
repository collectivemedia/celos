package com.collective.celos.ci.fixtures.plain;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.testing.TestContext;
import com.collective.celos.ci.fixtures.AbstractFixtureWorkerFactory;
import com.collective.celos.ci.fixtures.ComparatorFixtureWorker;
import com.collective.celos.ci.fixtures.DeployFixtureWorker;

/**
 * Created by akonopko on 10/4/14.
 */
public class PlainFixtureWorkerFactory extends AbstractFixtureWorkerFactory {

    private final static String WORKER_TYPE = "PLAIN";

    public PlainFixtureWorkerFactory(CelosCiContext ciContext, TestContext testContext) {
        super(ciContext, testContext);
    }

    @Override
    public String getWorkerType() {
        return WORKER_TYPE;
    }

    @Override
    public DeployFixtureWorker createDeployWorker() {
        return new PlainFixtureDeployWorker();
    }

    @Override
    public ComparatorFixtureWorker createCompareWorker() {
        return new PlainFixtureComparatorWorker();
    }
}
