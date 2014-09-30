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

        ContextParser contextParser = new ContextParser(new CelosCiTargetParser(userName));
        ContextParser.Context context = contextParser.parse(args, userName);

        CelosCi celosCi = new CelosCi();
        if (context.celosCiContext.getMode() == CelosCiContext.Mode.TEST) {
            celosCi.onTestMode(context.celosCiContext, context.testContext);
        } else if (context.celosCiContext.getMode() == CelosCiContext.Mode.DEPLOY) {
            celosCi.onDeployMode(context.celosCiContext);
        } else if (context.celosCiContext.getMode() == CelosCiContext.Mode.UNDEPLOY) {
            celosCi.onUndeployMode(context.celosCiContext);
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
