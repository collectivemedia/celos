package com.collective.celos.cd;

import com.collective.celos.config.CliParser;
import com.collective.celos.config.Config;
import com.collective.celos.config.TargetParcer;
import com.collective.celos.cd.deployer.HdfsDeployer;
import com.collective.celos.cd.deployer.WorkflowFileDeployer;

/**
 * Created by akonopko on 9/9/14.
 */
public class CelosCd {

    public static void main(String... commandLineArguments) throws Exception {

        CliParser cliParser = new CliParser();

        Config config = cliParser.parse(commandLineArguments);
        if (config != null) {
            runFromConfig(config);
        } else {
            cliParser.printHelp(80, 5, 3, true, System.out);
        }


    }

    public static void runFromConfig(Config config) throws Exception {

        if (config.getTargetFile() != null) {
            TargetParcer parcer = new TargetParcer(config);
            parcer.process();
        }
        config.validate();

        WorkflowFileDeployer wfDeployer = new WorkflowFileDeployer(config);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(config);
        if (config.getMode() == Config.Mode.DEPLOY) {
            wfDeployer.deploy();
            hdfsDeployer.deploy();
        } else if (config.getMode() == Config.Mode.UNDEPLOY) {
            wfDeployer.undeploy();
            hdfsDeployer.undeploy();
        }
    }

}
