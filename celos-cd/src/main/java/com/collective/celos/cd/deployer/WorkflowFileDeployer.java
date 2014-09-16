package com.collective.celos.cd.deployer;

import com.collective.celos.cd.config.CelosCdContext;
import org.apache.commons.vfs2.*;

import java.io.File;
import java.net.URISyntaxException;


public class WorkflowFileDeployer {

    private static final String CELOS_WORKFLOW_FILENAME = "workflow.js";

    private JScpWorker jScpWorker;
    private CelosCdContext config;

    public WorkflowFileDeployer(CelosCdContext config) throws FileSystemException {
        this.config = config;
        this.jScpWorker = new JScpWorker(config.getUserName(), config.getTarget().getScpSecuritySettings());
    }

    public void undeploy() throws FileSystemException, URISyntaxException {

        FileObject sftpFile = jScpWorker.getFileObjectByUri(getWorkflowJsUri());
        sftpFile.delete();

    }

    public void deploy() throws FileSystemException, URISyntaxException {

        File localFile = new File(config.getPathToWorkflow(), CELOS_WORKFLOW_FILENAME);

        if (!localFile.exists()) {
            throw new RuntimeException("Local file " + localFile.getAbsolutePath() + " was not found");
        }

        FileObject sftpFile = jScpWorker.getFileObjectByUri(getWorkflowJsUri());
        sftpFile.delete();

        FileObject localFileObject = jScpWorker.getFileObjectByUri(localFile.getAbsolutePath());
        sftpFile.copyFrom(localFileObject, Selectors.SELECT_SELF);
    }

    private String getWorkflowJsUri() {
        String celosWorkflowsDirUri = config.getCelosWorkflowsDirUri() != null ? config.getCelosWorkflowsDirUri() : config.getTarget().getCelosWorkflowsDirUri();
        return celosWorkflowsDirUri + File.separator + config.getWorkflowName() + ".js";
    }


}
