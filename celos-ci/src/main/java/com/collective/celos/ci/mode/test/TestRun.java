package com.collective.celos.ci.mode.test;

import com.collective.celos.Util;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.deploy.HdfsDeployer;
import com.collective.celos.ci.deploy.JScpWorker;
import com.collective.celos.ci.deploy.WorkflowFilesDeployer;
import com.collective.celos.ci.mode.test.client.CelosClient;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.FixtureComparer;
import com.collective.celos.ci.testing.fixtures.deploy.FixtureDeployer;
import com.collective.celos.server.CelosServer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestRun {

    public static final String CELOS_USER_JS_VAR = "CELOS_USER_JS_VAR";

    private static final String HDFS_PREFIX_PATTERN = "/user/%s/.celos-ci/%s/%s";
    private static final String HDFS_PREFIX_JS_VAR = "HDFS_PREFIX_JS_VAR";
    private static final String TEST_UUID_JS_VAR = "TEST_UUID_JS_VAR";
    private static final String WORKFLOW_DIR_CELOS_PATH = "workflows";
    private static final String DEFAULTS_DIR_CELOS_PATH = "defaults";
    private static final String DB_DIR_CELOS_PATH = "db";
    private static final String UI_DIR_CELOS_PATH = "ui";

    private final WorkflowFilesDeployer wfDeployer;
    private final HdfsDeployer hdfsDeployer;
    private final CelosCiContext ciContext;
    private final File celosWorkflowDir;
    private final File celosDefaultsDir;
    private final File celosDbDir;
    private final File celosUiDir;
    private final File testCaseTempDir;
    private final String hdfsPrefix;
    private final TestCase testCase;
    private final File testCasesDir;
    private final UUID testUUID;
    private final CelosCiTarget originalTarget;
    private final boolean keepTempData;

    public TestRun(CelosCiTarget target, CelosCiCommandLine commandLine, TestCase testCase, File celosCiTempDir) throws Exception {

        this.testCase = testCase;
        this.testCasesDir = commandLine.getTestCasesDir();

        testUUID = UUID.randomUUID();
        String hdfsPrefix = String.format(HDFS_PREFIX_PATTERN, commandLine.getUserName(), commandLine.getWorkflowName(), testUUID);

        this.hdfsPrefix = Util.requireNonNull(hdfsPrefix);
        this.testCaseTempDir = new File(celosCiTempDir, testUUID.toString());

        this.celosWorkflowDir = new File(testCaseTempDir, WORKFLOW_DIR_CELOS_PATH);
        this.celosDefaultsDir = new File(testCaseTempDir, DEFAULTS_DIR_CELOS_PATH);
        this.celosDbDir = new File(testCaseTempDir, DB_DIR_CELOS_PATH);
        this.celosUiDir = new File(testCaseTempDir, UI_DIR_CELOS_PATH);

        this.originalTarget = target;

        CelosCiTarget testTarget = new CelosCiTarget(target.getPathToHdfsSite(), target.getPathToCoreSite(), celosWorkflowDir.toURI(), celosDefaultsDir.toURI(), target.getHiveJdbc());
        this.ciContext = new CelosCiContext(testTarget, commandLine.getUserName(), CelosCiContext.Mode.TEST, commandLine.getDeployDir(), commandLine.getWorkflowName(), hdfsPrefix);

        this.wfDeployer = new WorkflowFilesDeployer(ciContext);
        this.hdfsDeployer = new HdfsDeployer(ciContext);
        this.keepTempData = commandLine.isKeepTempData();
    }

    public UUID getTestUUID() {
        return testUUID;
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

    public File getTestCaseTempDir() {
        return testCaseTempDir;
    }

    public File getTestCasesDir() {
        return testCasesDir;
    }

    public String getHdfsPrefix() {
        return hdfsPrefix;
    }

    public CelosCiContext getCiContext() {
        return ciContext;
    }

    public void start() throws Exception {

        System.out.println("Running test case " + testCase.getName());
        System.out.println(testCase.getName() + ": temp dir for Celos is " + testCaseTempDir.getAbsolutePath().toString());
        System.out.println(testCase.getName() + ": HDFS prefix is: " + hdfsPrefix);

        List<FixObjectCompareResult> results = executeTestRun();

        if (isTestRunFailed(results)) {
            throw new TestRunFailedException(getComparisonResults(results));
        } else {
            System.out.println(testCase.getName() + ": Real and expected fixtures matched");
        }

    }

    private List<FixObjectCompareResult> executeTestRun() throws Exception {

        final CelosServer celosServer = new CelosServer();

        try {
            Map additionalJSParams = ImmutableMap.of(
                    HDFS_PREFIX_JS_VAR, hdfsPrefix,
                    TEST_UUID_JS_VAR, testUUID,
                    CELOS_USER_JS_VAR, ciContext.getUserName());

            prepareCelosServerEnv();

            logJsFileExists(WorkflowFilesDeployer.WORKFLOW_FILENAME);
            logJsFileExists(WorkflowFilesDeployer.DEFAULTS_FILENAME);

            wfDeployer.deploy();
            hdfsDeployer.deploy();

            Integer port = celosServer.startServer(additionalJSParams, celosWorkflowDir, celosDefaultsDir, celosDbDir, celosUiDir);

            for (FixtureDeployer fixtureDeployer : testCase.getInputs()) {
                fixtureDeployer.deploy(this);
            }
            CelosClient client = new CelosClient("http://localhost:" + port);
            new CelosSchedulerWorker(client).runCelosScheduler(testCase);

            List<FixObjectCompareResult> results = Lists.newArrayList();
            for (FixtureComparer fixtureComparer : testCase.getOutputs()) {
                results.add(fixtureComparer.check(this));
            }
            return results;
        } finally {
            System.out.println(testCase.getName() + ": Stopping Celos");
            System.out.flush();
            celosServer.stopServer();
            if (!keepTempData) {
                doCleanup();
                validateCleanState();
            }
        }
    }

    private void logJsFileExists(String fileName) {
        File localFile = new File(ciContext.getDeployDir(), fileName);
        if (!localFile.exists()) {
            System.out.println(testCase.getName() + ": " + localFile.getAbsolutePath() + " was not found, so not deploying it");
        }
    }

    private void validateCleanState() throws Exception {
        for (FixtureDeployer fixtureDeployer : testCase.getInputs()) {
            fixtureDeployer.validate(this);
        }
    }

    private boolean isTestRunFailed(List<FixObjectCompareResult> results) {
        for (FixObjectCompareResult result : results) {
            if (result.getStatus() == FixObjectCompareResult.Status.FAIL) {
                return true;
            }
        }
        return false;
    }

    private String getComparisonResults(List<FixObjectCompareResult> results) throws IOException {
        List<String> messages = Lists.newArrayList();
        for (FixObjectCompareResult result : results) {
            if (result.getStatus() == FixObjectCompareResult.Status.FAIL) {
                messages.add(result.generateDescription());
            }
        }
        return StringUtils.join(messages, "\n");
    }

    private void doCleanup() throws Exception {
        FileUtils.forceDelete(testCaseTempDir);
        for (FixtureDeployer fixtureDeployer : testCase.getInputs()) {
            fixtureDeployer.undeploy(this);
        }
        ciContext.getFileSystem().delete(new org.apache.hadoop.fs.Path(ciContext.getHdfsPrefix()), true);
    }

    public TestCase getTestCase() {
        return testCase;
    }

    void prepareCelosServerEnv() throws IOException, URISyntaxException {

        celosWorkflowDir.mkdirs();
        celosDefaultsDir.mkdirs();
        celosDbDir.mkdirs();

        copyRemoteDefaultsToLocal();
    }

    private void copyRemoteDefaultsToLocal()
            throws URISyntaxException, FileSystemException {
        JScpWorker worker = new JScpWorker(ciContext.getUserName());
        if (originalTarget.getDefaultsDirUri() != null) {
            FileObject remoteDefaultsDir = worker.getFileObjectByUri(originalTarget.getDefaultsDirUri());
            if (remoteDefaultsDir.exists()) {
                FileObject localDefaultsDir = worker.getFileObjectByUri(celosDefaultsDir.toURI());
                localDefaultsDir.copyFrom(remoteDefaultsDir, Selectors.SELECT_CHILDREN);
            }
        }
    }
}
