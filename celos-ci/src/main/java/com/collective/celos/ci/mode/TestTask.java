package com.collective.celos.ci.mode;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.mode.test.TestCase;
import com.collective.celos.ci.mode.test.TestConfigurationParser;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestTask extends CelosCi {

    private final List<TestRun> testRuns = Lists.newArrayList();
    private static final String TEST_CONFIG_JS_FILE = "test.js";

    public TestTask(CelosCiCommandLine commandLine) throws Exception {
        this(commandLine, new File(commandLine.getTestCasesDir(), TEST_CONFIG_JS_FILE));
    }

    TestTask(CelosCiCommandLine commandLine, File configJSFile) throws Exception {
        CelosCiTargetParser parser = new CelosCiTargetParser(commandLine.getUserName());
        CelosCiTarget target = parser.parse(commandLine.getTargetUri());

        TestConfigurationParser configurationParser = new TestConfigurationParser();
        configurationParser.evaluateTestConfig(configJSFile);

        for (TestCase testCase : configurationParser.getTestCases()) {
            testRuns.add(new TestRun(target, commandLine, testCase));
        }
    }


    @Override
    public void start() throws Throwable {

        List<Future> futures = submitTestRuns();
        printAndThrowExceptions(futures);
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

    void printAndThrowExceptions(List<Future> futures) throws Throwable {
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
