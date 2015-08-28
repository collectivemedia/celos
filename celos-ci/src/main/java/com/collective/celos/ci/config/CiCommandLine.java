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
        this.mode = CelosCiContext.Mode.valueOf(Util.requireNonNull(mode));
        this.deployDir = new File(Util.requireNonNull(deployDir));
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
