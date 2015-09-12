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
package com.collective.celos.ci.config;


import com.collective.celos.Util;
import com.collective.celos.ci.config.deploy.CelosCiContext;

import java.io.File;
import java.net.URI;
import java.util.regex.Pattern;

/**
 * Created by akonopko on 9/30/14.
 */
public class CiCommandLine {

    private static final Pattern hdfsRootPattern = Pattern.compile("/[^/]?(.*)[^/]");

    private final URI targetUri;
    private final CelosCiContext.Mode mode;
    private final File deployDir;
    private final String workflowName;
    private final File testCasesDir;
    private final String userName;
    private final boolean keepTempData;
    private final URI celosServerUrl;
    private final String hdfsRoot;

    public CiCommandLine(String targetUri, String mode, String deployDir, String workflowName, String testCasesDir, String userName, boolean keepTempData, String celosServerUrl, String hdfsRoot) {
        this.hdfsRoot = getValidateHdfsRoot(hdfsRoot);
        this.celosServerUrl = celosServerUrl == null ? null : URI.create(celosServerUrl);
        this.userName = Util.requireNonNull(userName);
        this.keepTempData = keepTempData;
        this.targetUri = URI.create(Util.requireNonNull(targetUri));
        this.mode = CelosCiContext.Mode.valueOf(Util.requireNonNull(mode).toUpperCase());
        this.deployDir = getValidateDeployDir(this.mode, deployDir);
        this.workflowName = Util.requireNonNull(workflowName);
        this.testCasesDir = getValidateTestCasesDir(this.mode, testCasesDir);
    }

    private String getValidateHdfsRoot(String hdfsRoot) {
        if (hdfsRootPattern.matcher(hdfsRoot).matches()) {
            return hdfsRoot;
        } else {
            throw new IllegalArgumentException("HDFS root should start with single '/' symbol, and should end with no '/' symbol");
        }
    }

    private File getValidateDeployDir(CelosCiContext.Mode mode, String deployDir) {
        if (mode == CelosCiContext.Mode.TEST || mode == CelosCiContext.Mode.DEPLOY) {
            File file = new File(Util.requireNonNull(deployDir));
            if (!file.isDirectory()) {
                throw new IllegalArgumentException("Deploy directory was not found on default path " + file.getAbsolutePath() + ", please specify --deployDir parameter");
            }
            return file;
        }
        return null;
    }

    private File getValidateTestCasesDir(CelosCiContext.Mode mode, String testCasesDir) {
        if (mode == CelosCiContext.Mode.TEST) {
            File file = new File(Util.requireNonNull(testCasesDir));
            if (!file.isDirectory()) {
                throw new IllegalArgumentException("Directory with Celos-CI test cases was not found on default path " + file.getAbsolutePath() + ", please specify --testDir parameter");
            }
            return file;
        }
        return null;
    }

    public URI getTargetUri() {
        return targetUri;
    }

    public CelosCiContext.Mode getMode() {
        return mode;
    }

    public File getDeployDir() {
        return deployDir;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public File getTestCasesDir() {
        return testCasesDir;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isKeepTempData() {
        return keepTempData;
    }

    public URI getCelosServerUrl() {
        return celosServerUrl;
    }

    public String getHdfsRoot() {
        return hdfsRoot;
    }
}
