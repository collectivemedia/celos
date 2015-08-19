package com.collective.celos.ci.mode.test;

import java.net.URI;

/**
 * Created by akonopko on 12.08.15.
 */
interface TestRunCelosServerMode {

    URI startServer(TestRun testRun) throws Exception;

    void stopServer(TestRun testRun);

    URI getCelosWorkflowDir();

    URI getCelosDefaultsDir();

    String getHdfsPrefix();
}
