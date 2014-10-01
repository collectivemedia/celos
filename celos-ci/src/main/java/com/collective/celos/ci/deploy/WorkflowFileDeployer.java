package com.collective.celos.ci.deploy;

import com.collective.celos.Util;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;


public class WorkflowFileDeployer {

    private static final String CELOS_WORKFLOW_FILENAME = "workflow.js";

    private final JScpWorker jScpWorker;
    private final CelosCiContext config;

    public WorkflowFileDeployer(CelosCiContext context) throws FileSystemException {
        this.config = Util.requireNonNull(context);
        this.jScpWorker = new JScpWorker(context.getUserName());
    }

    public void undeploy() throws FileSystemException, URISyntaxException {

        FileObject sftpFile = jScpWorker.getFileObjectByUri(getWorkflowJsUri());
        sftpFile.delete();

    }

    public void deploy() throws FileSystemException, URISyntaxException {

        File localFile = new File(config.getDeployDir(), CELOS_WORKFLOW_FILENAME);

        if (!localFile.exists()) {
            throw new IllegalStateException("Local file " + localFile.getAbsolutePath() + " was not found");
        }

        undeploy();

        FileObject sftpFile = jScpWorker.getFileObjectByUri(getWorkflowJsUri());
        FileObject localFileObject = jScpWorker.getFileObjectByUri(localFile.getAbsolutePath());
        sftpFile.copyFrom(localFileObject, Selectors.SELECT_SELF);
    }

    URI getWorkflowJsUri() throws URISyntaxException {
        URI uri = config.getTarget().getCelosWorkflowsDirUri();
        return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                uri.getPath() + File.separator + config.getWorkflowName() + ".js", uri.getQuery(),
                uri.getFragment());
    }


}
