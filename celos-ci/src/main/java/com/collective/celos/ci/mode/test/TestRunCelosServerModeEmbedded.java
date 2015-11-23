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
package com.collective.celos.ci.mode.test;

import com.collective.celos.Util;
import com.collective.celos.ci.config.CiCommandLine;
import com.collective.celos.ci.deploy.JScpWorker;
import com.collective.celos.ci.deploy.WorkflowFilesDeployer;
import com.collective.celos.database.FileSystemStateDatabase;
import com.collective.celos.server.CelosServer;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestRunCelosServerModeEmbedded implements TestRunCelosServerMode {

    private static final String HDFS_PREFIX_PATTERN = "/user/%s/.celos-ci/%s/%s";
    private static final String HDFS_PREFIX_JS_VAR = "HDFS_PREFIX_JS_VAR";
    private static final String TEST_UUID_JS_VAR = "TEST_UUID_JS_VAR";
    private static final String CELOS_USER_JS_VAR = "CELOS_USER_JS_VAR";
    private static final String WORKFLOW_DIR_CELOS_PATH = "workflows";
    private static final String DEFAULTS_DIR_CELOS_PATH = "defaults";
    private static final String DB_DIR_CELOS_PATH = "db";


    private final CelosServer celosServer = new CelosServer();
    private final File workflowsDir;
    private final File defaultsDir;
    private final File stateDatabase;
    private final String hdfsPrefix;

    public TestRunCelosServerModeEmbedded(CiCommandLine commandLine, File testCaseTempDir, UUID testUUID) {
        this.workflowsDir = new File(testCaseTempDir, WORKFLOW_DIR_CELOS_PATH);
        this.defaultsDir = new File(testCaseTempDir, DEFAULTS_DIR_CELOS_PATH);
        this.stateDatabase = new File(testCaseTempDir, DB_DIR_CELOS_PATH);
        this.hdfsPrefix = String.format(HDFS_PREFIX_PATTERN, commandLine.getUserName(), commandLine.getWorkflowName(), testUUID);
    }

    public String getHdfsPrefix() {
        return this.hdfsPrefix;
    }

    public URI startServer(TestRun testRun) throws Exception {
        Map additionalJSParams = ImmutableMap.of(
                HDFS_PREFIX_JS_VAR, hdfsPrefix,
                TEST_UUID_JS_VAR, testRun.getTestUUID(),
                CELOS_USER_JS_VAR, testRun.getCiContext().getUserName());

        workflowsDir.mkdirs();
        defaultsDir.mkdirs();
        stateDatabase.mkdirs();

        FileSystemStateDatabase.Config config = new FileSystemStateDatabase.Config(stateDatabase);
        Integer port = celosServer.startServer(additionalJSParams, workflowsDir, defaultsDir, config);

        logJsFileExists(WorkflowFilesDeployer.WORKFLOW_FILENAME, testRun);
        logJsFileExists(WorkflowFilesDeployer.DEFAULTS_FILENAME, testRun);

        copyRemoteDefaultsToLocal(testRun.getCiContext().getUserName(), testRun.getOriginalTarget().getDefaultsDirUri());

        return URI.create("http://localhost:" + port);
    }

    private void logJsFileExists(String fileName, TestRun testRun) {
        File localFile = new File(testRun.getCiContext().getDeployDir(), fileName);
        if (!localFile.exists()) {
            System.out.println(testRun.getTestCase().getName() + ": " + localFile.getAbsolutePath() + " was not found, so not deploying it");
        }
    }

    public void copyRemoteDefaultsToLocal(String username, URI defaultsDirUri)
            throws URISyntaxException, FileSystemException {
        JScpWorker worker = new JScpWorker(username);
        if (defaultsDirUri != null) {
            FileObject remoteDefaultsDir = worker.getFileObjectByUri(defaultsDirUri);
            if (remoteDefaultsDir.exists()) {
                FileObject localDefaultsDir = worker.getFileObjectByUri(getCelosDefaultsDir());
                localDefaultsDir.copyFrom(remoteDefaultsDir, Selectors.SELECT_CHILDREN);
            }
        }
    }


    public void stopServer(TestRun testRun) {
        try {
            System.out.println(testRun.getTestCase().getName() + ": Stopping Celos");
            System.out.flush();
            celosServer.stopServer();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public URI getCelosWorkflowDir() {
        return workflowsDir.toURI();
    }

    public URI getCelosDefaultsDir() {
        return defaultsDir.toURI();
    }

}
