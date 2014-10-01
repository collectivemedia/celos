package com.collective.celos.ci.config.testing;

import java.io.File;

/**
 * Created by akonopko on 9/24/14.
 */
public class TestContext {

    private static final String WORKFLOW_DIR_CELOS_PATH = "workflows";
    private static final String DEFAULTS_DIR_CELOS_PATH = "defaults";
    private static final String DB_DIR_CELOS_PATH = "db";

    private final File celosWorkflowDir;
    private final File celosDefaultsDir;
    private final File celosDbDir;
    private final File celosWorkDir;
    private final String hdfsPrefix;
    private final File testCasesDir;

    public TestContext(File celosWorkDir, String hdfsPrefix, File testCasesDir) {
        this.celosWorkDir = celosWorkDir;
        this.hdfsPrefix = hdfsPrefix;
        this.testCasesDir = testCasesDir;

        this.celosWorkflowDir = new File(celosWorkDir, WORKFLOW_DIR_CELOS_PATH);
        this.celosDefaultsDir = new File(celosWorkDir, DEFAULTS_DIR_CELOS_PATH);
        this.celosDbDir = new File(celosWorkDir, DB_DIR_CELOS_PATH);
    }

    public File getCelosWorkflowDir() {
        return celosWorkflowDir;
    }

    public File getCelosDefaultsDir() {
        return celosDefaultsDir;
    }

    public File getCelosDbDir() {
        return celosDbDir;
    }

    public String getHdfsPrefix() {
        return hdfsPrefix;
    }

    public File getTestCasesDir() {
        return testCasesDir;
    }

    public File getCelosWorkDir() {
        return celosWorkDir;
    }
}
