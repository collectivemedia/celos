package com.collective.celos.ci.mode;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.mode.test.TestCase;
import com.collective.celos.ci.mode.test.TestConfigurationParser;
import com.collective.celos.ci.mode.test.TestRun;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestTask extends CelosCi {

    private static final String TEST_CONFIG_JS_FILE = "test.js";

    private final List<TestRun> testRuns = Lists.newArrayList();
    private final File celosCiTempDir;

    public TestTask(CelosCiCommandLine commandLine) throws Exception {
        this(commandLine, new File(commandLine.getTestCasesDir(), TEST_CONFIG_JS_FILE));
    }

    TestTask(CelosCiCommandLine commandLine, File configJSFile) throws Exception {

        this.celosCiTempDir = Files.createTempDirectory("celos").toFile();

        CelosCiTargetParser parser = new CelosCiTargetParser(commandLine.getUserName());
        CelosCiTarget target = parser.parse(commandLine.getTargetUri());

        TestConfigurationParser configurationParser = new TestConfigurationParser();
        configurationParser.evaluateTestConfig(configJSFile);

        for (TestCase testCase : configurationParser.getTestCases()) {
            testRuns.add(new TestRun(target, commandLine, testCase, celosCiTempDir));
        }
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
