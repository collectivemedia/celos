package com.collective.celos.cd;

import com.collective.celos.config.CelosCiContext;
import com.collective.celos.config.CelosCiContextBuilder;
import com.collective.celos.cd.deployer.HdfsDeployer;
import com.collective.celos.cd.deployer.WorkflowFileDeployer;

/**
 * Created by akonopko on 9/9/14.
 */
public class CelosCd {

    public static void main(String... commandLineArguments) throws Exception {

        CelosCiContextBuilder contextBuilder = new CelosCiContextBuilder();

        CelosCiContext config = contextBuilder.parse(commandLineArguments);
        if (config != null) {
            runForContext(config);
        } else {
            contextBuilder.printHelp(80, 5, 3, true, System.out);
        }


    }

    public static void runForContext(CelosCiContext config) throws Exception {

        WorkflowFileDeployer wfDeployer = new WorkflowFileDeployer(config);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(config);
        if (config.getMode() == CelosCiContext.Mode.DEPLOY || config.getMode() == CelosCiContext.Mode.TEST) {
            wfDeployer.deploy();
            hdfsDeployer.deploy();
        } else if (config.getMode() == CelosCiContext.Mode.UNDEPLOY) {
            wfDeployer.undeploy();
            hdfsDeployer.undeploy();
        }
    }

}
