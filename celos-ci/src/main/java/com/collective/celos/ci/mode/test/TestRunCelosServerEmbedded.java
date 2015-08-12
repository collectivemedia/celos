package com.collective.celos.ci.mode.test;

import com.collective.celos.Util;
import com.collective.celos.server.CelosServer;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestRunCelosServerEmbedded implements TestRunCelosServer {

    private static final String HDFS_PREFIX_JS_VAR = "HDFS_PREFIX_JS_VAR";
    private static final String TEST_UUID_JS_VAR = "TEST_UUID_JS_VAR";
    public static final String CELOS_USER_JS_VAR = "CELOS_USER_JS_VAR";

    private final CelosServer celosServer = new CelosServer();
    private final TestRun testRun;

    public TestRunCelosServerEmbedded(TestRun testRun) {
        this.testRun = testRun;
    }

    public URI startServer() throws Exception {
        Map additionalJSParams = ImmutableMap.of(
                HDFS_PREFIX_JS_VAR, testRun.getHdfsPrefix(),
                TEST_UUID_JS_VAR, testRun.getTestUUID(),
                CELOS_USER_JS_VAR, testRun.getCiContext().getUserName());

        prepareCelosServerEnv(testRun);

        Integer port = Util.getFreePort();
        celosServer.startServer(port, additionalJSParams, testRun.getCelosWorkflowDir(), testRun.getCelosDefaultsDir(), testRun.getCelosDbDir(), testRun.getCelosUiDir());

        return URI.create("http://localhost:" + port);
    }

    public void stopServer() {
        try {
            System.out.println(testRun.getTestCase().getName() + ": Stopping Celos");
            System.out.flush();
            celosServer.stopServer();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    void prepareCelosServerEnv(TestRun testRun) throws IOException, URISyntaxException {

        testRun.getCelosWorkflowDir().mkdirs();
        testRun.getCelosDefaultsDir().mkdirs();
        testRun.getCelosDbDir().mkdirs();
        testRun.getCelosUiDir().mkdirs();
    }


}
