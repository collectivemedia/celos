package com.collective.celos.cd;

import com.collective.celos.cd.config.CelosCdContext;
import com.collective.celos.cd.config.CelosCdContextBuilder;
import com.collective.celos.cd.deployer.HdfsDeployer;
import com.collective.celos.cd.deployer.WorkflowFileDeployer;

/**
 * Created by akonopko on 9/9/14.
 */
public class CelosCd {

    public static void main(String... commandLineArguments) throws Exception {

        CelosCdContextBuilder contextBuilder = new CelosCdContextBuilder();

        CelosCdContext config = contextBuilder.parse(commandLineArguments);
        if (config != null) {
            runForContext(config);
        } else {
            contextBuilder.printHelp(80, 5, 3, true, System.out);
        }


    }

    public static void runForContext(CelosCdContext config) throws Exception {

        WorkflowFileDeployer wfDeployer = new WorkflowFileDeployer(config);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(config);
        if (config.getMode() == CelosCdContext.Mode.DEPLOY) {
            wfDeployer.deploy();
            hdfsDeployer.deploy();
        } else if (config.getMode() == CelosCdContext.Mode.UNDEPLOY) {
            wfDeployer.undeploy();
            hdfsDeployer.undeploy();
        }
    }

}
