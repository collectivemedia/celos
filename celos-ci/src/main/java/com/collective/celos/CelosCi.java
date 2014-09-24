package com.collective.celos;

import com.collective.celos.config.ContextParser;
import com.collective.celos.config.ci.CelosCiContext;
import com.collective.celos.config.test.TestContext;
import com.collective.celos.deploy.HdfsDeployer;
import com.collective.celos.deploy.WorkflowFileDeployer;
import com.collective.celos.testing.CelosCiIntegrationTestRunner;

public class CelosCi {

    public static void main(String... args) throws Exception {

//        args = "--deployDir /home/akonopko/work/celos2/samples/wordcount2 --target sftp://107.170.177.172/home/akonopko/target.json --workflowName wordcount".split(" ");
//        args = ("--deployDir /home/akonopko/work/celos2/samples/wordcount/build/celos_deploy " +
//                "--target sftp://celos001/home/akonopko/target.json --workflowName wordcount " +
//                "--mode TEST --testDir /home/akonopko/work/celos2/samples/wordcount/src/test").split(" ");

        ContextParser contextParser = new ContextParser();
        contextParser.parse(args, new CelosCi());
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
