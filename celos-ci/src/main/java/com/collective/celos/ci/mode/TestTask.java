package com.collective.celos.ci.mode;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.mode.test.TestConfigurationParser;
import com.collective.celos.ci.mode.test.TestRun;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestTask extends CelosCi {

    List<TestRun> testRuns = Lists.newArrayList();

    public TestTask(CelosCiCommandLine commandLine) throws Exception {

        CelosCiTargetParser parser = new CelosCiTargetParser(commandLine.getUserName());
        CelosCiTarget target = parser.parse(commandLine.getTargetUri());

        TestConfigurationParser testConfigurationParser = new TestConfigurationParser();
        testConfigurationParser.parseFile(new File(commandLine.getTestCasesDir(), "test.js"));
    }

    @Override
    public void start() throws Exception {
        for (TestRun testRun : testRuns) {
            testRun.start();
        }
    }

}
