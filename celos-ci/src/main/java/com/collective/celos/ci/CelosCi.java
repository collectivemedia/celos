package com.collective.celos.ci;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.ContextParser;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.mode.DeployTask;
import com.collective.celos.ci.mode.TestTask;
import com.collective.celos.ci.mode.UndeployTask;

public abstract class CelosCi {

    public static void main(String... args) throws Exception {

        ContextParser contextParser = new ContextParser();
        CelosCiCommandLine commandLine = contextParser.parse(args);

        CelosCi celosCi = createCelosCi(commandLine);
        celosCi.start();
    }

    public static CelosCi createCelosCi(CelosCiCommandLine commandLine) throws Exception {

        if (commandLine.getMode() == CelosCiContext.Mode.TEST) {
            return new TestTask(commandLine);
        } else if (commandLine.getMode() == CelosCiContext.Mode.DEPLOY) {
            return new DeployTask(commandLine);
        } else if (commandLine.getMode() == CelosCiContext.Mode.UNDEPLOY) {
            return new UndeployTask(commandLine);
        }
        throw new IllegalStateException("Unknown mode " + commandLine.getMode());
    }

    public abstract void start() throws Exception;

}
