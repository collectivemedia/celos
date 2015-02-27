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

    public static final String WORKFLOW_FILENAME = "workflow.js";
    public static final String DEFAULTS_FILENAME = "defaults.js";

    private final JScpWorker jScpWorker;
    private final CelosCiContext config;

    public WorkflowFilesDeployer(CelosCiContext context) throws FileSystemException {
        this.config = Util.requireNonNull(context);
        this.jScpWorker = new JScpWorker(context.getUserName());
    }

    public void undeploy() throws FileSystemException, URISyntaxException {
        undeployJSFile(config.getTarget().getWorkflowsDirUri());
        undeployJSFile(config.getTarget().getDefaultsDirUri());
    }

    private void undeployJSFile(URI dirUri) throws URISyntaxException, FileSystemException {
        if (dirUri != null) {
            FileObject file = jScpWorker.getFileObjectByUri(getTargetJsFileUri(dirUri));
            file.delete();
        }
    }

    public void deploy() throws FileSystemException, URISyntaxException {
        undeploy();
        deployJSFile(config.getTarget().getWorkflowsDirUri(), WORKFLOW_FILENAME);
        deployJSFile(config.getTarget().getDefaultsDirUri(), DEFAULTS_FILENAME);
    }

    private void deployJSFile(URI dirUri, String fileName) throws URISyntaxException, FileSystemException {
        if (dirUri != null) {
            URI fileUri = getTargetJsFileUri(dirUri);
            File localFile = new File(config.getDeployDir(), fileName);
            if (localFile.exists()) {
                FileObject sftpFile = jScpWorker.getFileObjectByUri(fileUri);
                FileObject localFileObject = jScpWorker.getFileObjectByUri(localFile.getAbsolutePath());
                sftpFile.copyFrom(localFileObject, Selectors.SELECT_SELF);
            }
        }
    }

    URI getTargetJsFileUri(URI uri) throws URISyntaxException {
        String path = uri.getPath() + File.separator + config.getWorkflowName() + ".js";
        return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(),
                uri.getPort(), path, uri.getQuery(), uri.getFragment());
    }


}
