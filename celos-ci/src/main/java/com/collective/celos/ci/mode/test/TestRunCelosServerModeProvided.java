package com.collective.celos.ci.mode.test;

import com.collective.celos.ci.config.CiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiTarget;

import java.net.URI;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestRunCelosServerModeProvided implements TestRunCelosServerMode {

    private static final String HDFS_PREFIX_PATTERN = "/user/%s/celos.selftest";

    private final URI celosServerUrl;
    private final CelosCiTarget target;
    private final String hdfsPrefix;

    public TestRunCelosServerModeProvided(CiCommandLine commandLine, CelosCiTarget target) {
        this.celosServerUrl = commandLine.getCelosServerUrl();
        this.target = target;
        this.hdfsPrefix = String.format(HDFS_PREFIX_PATTERN, commandLine.getUserName());
    }

    public URI startServer(TestRun testRun) throws Exception {
        return celosServerUrl;
    }

    public void stopServer(TestRun testRun) {
    }

    @Override
    public String getHdfsPrefix() {
        return hdfsPrefix;
    }

    @Override
    public URI getCelosWorkflowDir() {
        return target.getWorkflowsDirUri();
    }

    @Override
    public URI getCelosDefaultsDir() {
        return target.getDefaultsDirUri();
    }

}
