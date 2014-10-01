package com.collective.celos.ci.mode;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;

/**
 * Created by akonopko on 10/1/14.
 */
public class CelosCiDeploy extends CelosCi {

    private final CelosCiContext ciContext;

    public CelosCiDeploy(CelosCiCommandLine commandLine) throws Exception {
        CelosCiTargetParser parser = new CelosCiTargetParser(commandLine.getUserName());
        CelosCiTarget target = parser.parse(commandLine.getTargetUri());
        this.ciContext = new CelosCiContext(target, commandLine.getUserName(), commandLine.getMode(), commandLine.getDeployDir(), commandLine.getWorkflowName(), "");
    }

    public CelosCiContext getCiContext() {
        return this.ciContext;
    }

    @Override
    public void start() throws Exception {
        //TODO:
    }

}
