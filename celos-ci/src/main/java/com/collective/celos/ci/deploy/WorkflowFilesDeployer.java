package com.collective.celos.ci.deploy;

import com.collective.celos.Util;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;


public class WorkflowFilesDeployer {

    private static final String WORKFLOW_FILENAME = "workflow.js";
    private static final String DEFAULTS_FILENAME = "defaults.js";

    private final JScpWorker jScpWorker;
    private final CelosCiContext config;

    public WorkflowFilesDeployer(CelosCiContext context) throws FileSystemException {
        this.config = Util.requireNonNull(context);
        this.jScpWorker = new JScpWorker(context.getUserName());
    }

    public void undeploy() throws FileSystemException, URISyntaxException {

        URI workflowFileUri = getTargetJsFileUri(config.getTarget().getWorkflowsDirUri());
        if (workflowFileUri != null) {
            FileObject workflowFile = jScpWorker.getFileObjectByUri(workflowFileUri);
            workflowFile.delete();
        }

        URI defaultsFileUri = getTargetJsFileUri(config.getTarget().getDefaultsDirUri());
        if (defaultsFileUri != null) {
            FileObject defaultsFile = jScpWorker.getFileObjectByUri(defaultsFileUri);
            defaultsFile.delete();
        }

    }

    public void deploy() throws FileSystemException, URISyntaxException {

        undeploy();

        URI workflowFileUri = getTargetJsFileUri(config.getTarget().getWorkflowsDirUri());
        if (workflowFileUri != null) {
            File localWorkflowFile = new File(config.getDeployDir(), WORKFLOW_FILENAME);
            deployJSFile(localWorkflowFile, workflowFileUri);
        }

        URI defaultsFileUri = getTargetJsFileUri(config.getTarget().getDefaultsDirUri());
        if (defaultsFileUri != null) {
            File localDefaultsFile = new File(config.getDeployDir(), DEFAULTS_FILENAME);
            deployJSFile(localDefaultsFile, defaultsFileUri);
        }
    }

    private void deployJSFile(File localFile, URI remoteUri) throws URISyntaxException, FileSystemException {
        if (localFile.exists()) {
            FileObject sftpFile = jScpWorker.getFileObjectByUri(remoteUri);
            FileObject localFileObject = jScpWorker.getFileObjectByUri(localFile.getAbsolutePath());
            sftpFile.copyFrom(localFileObject, Selectors.SELECT_SELF);
        }
    }

    URI getTargetJsFileUri(URI uri) throws URISyntaxException {
        if (uri == null) {
            return null;
        } else {
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                    uri.getPath() + File.separator + config.getWorkflowName() + ".js", uri.getQuery(),
                    uri.getFragment());
        }
    }


}
