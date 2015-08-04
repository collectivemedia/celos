package com.collective.celos.ci.mode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.CiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.mode.test.TestCase;
import com.collective.celos.ci.mode.test.TestConfigurationParser;
import com.collective.celos.ci.mode.test.TestRun;
import com.google.common.collect.Lists;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestTask extends CelosCi {

    private static final String CELOS_CI_DIR = ".celos-ci";
    private static final String TEST_CONFIG_JS_FILE = "test.js";
    private static final String CELOS_LOG_FILE = "celos.log";

    private final List<TestRun> testRuns = Lists.newArrayList();
    private final File celosCiTempDir;

    public TestTask(CiCommandLine commandLine) throws Exception {
        this(commandLine, getConfigJSFile(commandLine), getTempDir());
    }

    TestTask(CiCommandLine commandLine, File configJSFile, File tempDir) throws Exception {

        this.celosCiTempDir = tempDir;
        substituteLoggers();

        CelosCiTargetParser parser = new CelosCiTargetParser(commandLine.getUserName());
        CelosCiTarget target = parser.parse(commandLine.getTargetUri());

        TestConfigurationParser configurationParser = new TestConfigurationParser();
        configurationParser.evaluateTestConfig(configJSFile);

        for (TestCase testCase : configurationParser.getTestCases()) {
            testRuns.add(new TestRun(target, commandLine, testCase, celosCiTempDir));
        }
    }

    private static File getConfigJSFile(CiCommandLine commandLine) {
        return new File(commandLine.getTestCasesDir(), TEST_CONFIG_JS_FILE);
    }

    static File getTempDir() throws IOException {
        File celosCiDir = new File(System.getProperty("user.home"), CELOS_CI_DIR);
        File tempDir = new File(celosCiDir, UUID.randomUUID().toString());
        return Files.createDirectories(tempDir.toPath(), getTempDirAttributes()).toFile();
    }

    private static FileAttribute<Set<PosixFilePermission>> getTempDirAttributes() {
        return PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x"));
    }

    private void substituteLoggers() throws IOException {
        //don't load configuration from log4j.xml -- we're doing this programmatically
        System.getProperties().setProperty("log4j.defaultInitOverride", "true");

        FileAppender fileAppender = new FileAppender();
        fileAppender.setFile(new File(celosCiTempDir, CELOS_LOG_FILE).getAbsolutePath());

        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setConversionPattern("[%d{YYYY-MM-dd HH:mm:ss.SSS}] [%t] %-5p: %m%n");
        fileAppender.setLayout(patternLayout);
        fileAppender.activateOptions();
        Logger.getRootLogger().addAppender(fileAppender);
        Logger.getRootLogger().setLevel(Level.INFO);
    }


    @Override
    public void start() throws Throwable {
        try {
            List<Future> futures = submitTestRuns();
            waitForCompletion(futures);
        } finally {
            if (allTestCasesDeletedTheirData()) {
                FileUtils.forceDelete(celosCiTempDir);
            }
        }
    }

    private boolean allTestCasesDeletedTheirData() {
        for (File child : celosCiTempDir.listFiles()) {
            if (child.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    private List<Future> submitTestRuns() {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future> futures = Lists.newArrayList();
        for (final TestRun testRun : testRuns) {
            Callable callable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    testRun.start();
                    return null;
                }
            };
            futures.add(executor.submit(callable));
        }
        return futures;
    }

    void waitForCompletion(List<Future> futures) throws Throwable {
        List<Throwable> throwables = Lists.newArrayList();
        for (Future future : futures) {
            try {
                future.get();
            } catch (ExecutionException ee) {
                throwables.add(ee.getCause());
            }
        }
        if (!throwables.isEmpty()) {
            List<Throwable> throwablesWOLast = throwables.subList(0, throwables.size() - 1);
            for (Throwable t : throwablesWOLast) {
                t.printStackTrace();
            }
            Throwable lastThrowable = throwables.get(throwables.size() - 1);
            throw lastThrowable;
        }
    }

    List<TestRun> getTestRuns() {
        return testRuns;
    }
}
