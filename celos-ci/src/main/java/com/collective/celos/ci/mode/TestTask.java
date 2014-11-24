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

/**
 * Created by akonopko on 10/1/14.
 */
public class TestTask extends CelosCi {

    private final List<TestRun> testRuns = Lists.newArrayList();
    private static final String TEST_CONFIG_JS_FILE_PATH = "src/test/celos-ci/test-config.js";

    public TestTask(CelosCiCommandLine commandLine) throws Exception {
        this(commandLine, TEST_CONFIG_JS_FILE_PATH);
    }

    TestTask(CelosCiCommandLine commandLine, String testConfigJsFilePath) throws Exception {
        CelosCiTargetParser parser = new CelosCiTargetParser(commandLine.getUserName());
        CelosCiTarget target = parser.parse(commandLine.getTargetUri());

        File configJSFile = new File(testConfigJsFilePath);

        TestConfigurationParser configurationParser = new TestConfigurationParser();
        configurationParser.evaluateTestConfig(configJSFile);

        for (TestCase testCase : configurationParser.getTestCases()) {
            testRuns.add(new TestRun(target, commandLine.getUserName(), commandLine.getWorkflowName(), commandLine.getDeployDir(), testCase));
        }
    }

    @Override
    public void start() throws Exception {
        for (TestRun testRun : testRuns) {
            testRun.start();
        }
    }

    List<TestRun> getTestRuns() {
        return testRuns;
    }
}
