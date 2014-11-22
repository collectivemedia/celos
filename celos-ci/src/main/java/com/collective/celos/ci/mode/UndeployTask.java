package com.collective.celos.ci.mode;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.deploy.HdfsDeployer;
import com.collective.celos.ci.deploy.WorkflowFileDeployer;

/**
 * Created by akonopko on 10/1/14.
 */
public class UndeployTask extends CelosCi {

    private final CelosCiContext ciContext;

    public UndeployTask(CelosCiCommandLine commandLine) throws Exception {
        CelosCiTargetParser parser = new CelosCiTargetParser(commandLine.getUserName());
        CelosCiTarget target = parser.parse(commandLine.getTargetUri());
        this.ciContext = new CelosCiContext(target, commandLine.getUserName(), commandLine.getMode(), commandLine.getDeployDir(), commandLine.getWorkflowName(), "", testUUID);
    }

    public CelosCiContext getCiContext() {
        return this.ciContext;
    }

    @Override
    public void start() throws Exception {
        WorkflowFileDeployer wfDeployer = new WorkflowFileDeployer(ciContext);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(ciContext);
        wfDeployer.undeploy();
        hdfsDeployer.undeploy();

    }

}
