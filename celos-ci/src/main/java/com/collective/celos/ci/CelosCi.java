package com.collective.celos.ci;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.ContextParser;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.mode.CelosCiDeploy;
import com.collective.celos.ci.mode.CelosCiTest;
import com.collective.celos.ci.mode.CelosCiUndeploy;

public abstract class CelosCi {

    public static void main(String... args) throws Exception {

        ContextParser contextParser = new ContextParser();
        CelosCiCommandLine commandLine = contextParser.parse(args);

        if (commandLine != null) {
            CelosCi celosCi = createCelosCi(commandLine);
            celosCi.start();
        }
    }

    public static CelosCi createCelosCi(CelosCiCommandLine commandLine) throws Exception {

        if (commandLine.getMode() == CelosCiContext.Mode.TEST) {
            return new CelosCiTest(commandLine);
        } else if (commandLine.getMode() == CelosCiContext.Mode.DEPLOY) {
            return new CelosCiDeploy(commandLine);
        } else if (commandLine.getMode() == CelosCiContext.Mode.UNDEPLOY) {
            return new CelosCiUndeploy(commandLine);
        }
        throw new IllegalStateException("Unknown mode " + commandLine.getMode());
    }

    public abstract void start() throws Exception;

}
