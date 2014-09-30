package com.collective.celos.ci;

import com.collective.celos.ci.config.ContextParser;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.config.testing.TestContext;
import com.collective.celos.ci.deploy.HdfsDeployer;
import com.collective.celos.ci.deploy.WorkflowFileDeployer;
import com.collective.celos.ci.testing.CelosCiIntegrationTestRunner;

public class CelosCi {

    public static void main(String... args) throws Exception {

        String userName = System.getenv("username") == null ? System.getProperty("user.name") : System.getenv("username");

        ContextParser contextParser = new ContextParser(new CelosCiTargetParser(userName), userName);
        CelosCi celosCi = contextParser.parse(args);

        celosCi.start();
    }

    public final CelosCiContext celosCiContext;
    public final TestContext testContext;

    public CelosCi(CelosCiContext celosCiContext, TestContext testContext) {
        this.celosCiContext = celosCiContext;
        this.testContext = testContext;
    }

    public void start() throws Exception {
        if (celosCiContext.getMode() == CelosCiContext.Mode.TEST) {
            onTestMode(celosCiContext, testContext);
        } else if (celosCiContext.getMode() == CelosCiContext.Mode.DEPLOY) {
            onDeployMode(celosCiContext);
        } else if (celosCiContext.getMode() == CelosCiContext.Mode.UNDEPLOY) {
            onUndeployMode(celosCiContext);
        }

    }

    public void onDeployMode(CelosCiContext ciContext) throws Exception {
        WorkflowFileDeployer wfDeployer = new WorkflowFileDeployer(ciContext);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(ciContext);
        wfDeployer.deploy();
        hdfsDeployer.deploy();
    }

    public void onUndeployMode(CelosCiContext ciContext) throws Exception {
        WorkflowFileDeployer wfDeployer = new WorkflowFileDeployer(ciContext);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(ciContext);
        wfDeployer.undeploy();
        hdfsDeployer.undeploy();
    }

    public void onTestMode(CelosCiContext ciContext, TestContext testContext) throws Exception {
        new CelosCiIntegrationTestRunner(ciContext, testContext).runTests();
    }


}
