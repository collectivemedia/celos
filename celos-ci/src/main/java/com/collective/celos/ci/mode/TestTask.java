package com.collective.celos.ci.mode;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.mode.test.TestRun;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestTask extends CelosCi {

    public static final String META_DIR_NAME = "meta";
    List<TestRun> testRuns = Lists.newArrayList();

    public TestTask(CelosCiCommandLine commandLine) throws Exception {

        CelosCiTargetParser parser = new CelosCiTargetParser(commandLine.getUserName());
        CelosCiTarget target = parser.parse(commandLine.getTargetUri());

        for (File tcDir : commandLine.getTestCasesDir().listFiles()) {
            if (tcDir.getName() != META_DIR_NAME) {
                testRuns.add(new TestRun(target, commandLine.getUserName(), commandLine.getWorkflowName(), commandLine.getDeployDir(), tcDir));
            }
        }
    }

    @Override
    public void start() throws Exception {
        for (TestRun testRun : testRuns) {
            testRun.start();
        }
    }

}
