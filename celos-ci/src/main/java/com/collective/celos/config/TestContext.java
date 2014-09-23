package com.collective.celos.config;

import java.io.File;

/**
 * Created by akonopko on 9/22/14.
 */
public class TestContext {

    private TestConfig testConfig;
    private File celosWorkflowDir;
    private File celosDefaultsDir;
    private File celosDbDir;
    private Integer celosPort;
    private String hdfsPrefix;

    public TestConfig getTestConfig() {
        return testConfig;
    }

    public void setTestConfig(TestConfig testConfig) {
        this.testConfig = testConfig;
    }

    public File getCelosWorkflowDir() {
        return celosWorkflowDir;
    }

    public void setCelosWorkflowDir(File celosWorkflowDir) {
        this.celosWorkflowDir = celosWorkflowDir;
    }

    public File getCelosDefaultsDir() {
        return celosDefaultsDir;
    }

    public void setCelosDefaultsDir(File celosDefaultsDir) {
        this.celosDefaultsDir = celosDefaultsDir;
    }

    public File getCelosDbDir() {
        return celosDbDir;
    }

    public void setCelosDbDir(File celosDbDir) {
        this.celosDbDir = celosDbDir;
    }

    public Integer getCelosPort() {
        return celosPort;
    }

    public void setCelosPort(Integer celosPort) {
        this.celosPort = celosPort;
    }

    public String getHdfsPrefix() {
        return hdfsPrefix;
    }

    public void setHdfsPrefix(String hdfsPrefix) {
        this.hdfsPrefix = hdfsPrefix;
    }
}
