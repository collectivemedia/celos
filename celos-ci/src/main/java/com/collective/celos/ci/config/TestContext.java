package com.collective.celos.ci.config;

import com.collective.celos.cd.config.CelosCdTarget;

import java.io.File;

public class TestContext {

    private final TestConfig testConfig;
    private final CelosCdTarget target;
    private final String workflowName;
    private final String deployDir;
    private final String userName;

    private File celosWorkflowDirUri;
    private File celosDefaultsDirUri;
    private File celosDbDirUri;
    private Integer celosPort;

    public TestContext(TestConfig workflowTestConfig, CelosCdTarget target, String workflowName,
                       String deployDir, String userName) {

        this.testConfig = workflowTestConfig;
        this.target = target;
        this.workflowName = workflowName;
        this.deployDir = deployDir;
        this.userName = userName;
    }

    public TestConfig getTestConfig() {
        return testConfig;
    }

    public CelosCdTarget getTarget() {
        return target;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public String getDeployDir() {
        return deployDir;
    }

    public Integer getCelosPort() {
        return celosPort;
    }

    public String getUserName() {
        return userName;
    }

    public File getCelosWorkflowDirUri() {
        return celosWorkflowDirUri;
    }

    public void setCelosWorkflowDirUri(File celosWorkflowDirUri) {
        this.celosWorkflowDirUri = celosWorkflowDirUri;
    }

    public File getCelosDefaultsDirUri() {
        return celosDefaultsDirUri;
    }

    public void setCelosDefaultsDirUri(File celosDefaultsDirUri) {
        this.celosDefaultsDirUri = celosDefaultsDirUri;
    }

    public File getCelosDbDirUri() {
        return celosDbDirUri;
    }

    public void setCelosDbDirUri(File celosDbDirUri) {
        this.celosDbDirUri = celosDbDirUri;
    }

    public void setCelosPort(Integer celosPort) {
        this.celosPort = celosPort;
    }
}