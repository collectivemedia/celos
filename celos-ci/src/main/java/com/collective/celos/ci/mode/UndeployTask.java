/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.ci.mode;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.CiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.deploy.HdfsDeployer;
import com.collective.celos.ci.deploy.WorkflowFilesDeployer;

/**
 * Created by akonopko on 10/1/14.
 */
public class UndeployTask extends CelosCi {

    private final CelosCiContext ciContext;

    public UndeployTask(CiCommandLine commandLine) throws Exception {
        CelosCiTargetParser parser = new CelosCiTargetParser(commandLine.getUserName());
        CelosCiTarget target = parser.parse(commandLine.getTargetUri());
        this.ciContext = new CelosCiContext(target, commandLine.getUserName(), commandLine.getMode(), commandLine.getDeployDir(), commandLine.getWorkflowName(), "", commandLine.getHdfsRoot());
    }

    public CelosCiContext getCiContext() {
        return this.ciContext;
    }

    @Override
    public void start() throws Exception {
        WorkflowFilesDeployer wfDeployer = new WorkflowFilesDeployer(ciContext);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(ciContext);
        wfDeployer.undeploy();
        hdfsDeployer.undeploy();

    }

}
