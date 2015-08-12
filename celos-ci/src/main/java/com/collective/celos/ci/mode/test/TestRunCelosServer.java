package com.collective.celos.ci.mode.test;

import java.net.URI;
import java.net.URL;

/**
 * Created by akonopko on 12.08.15.
 */
public interface TestRunCelosServer {

    URI startServer() throws Exception;

    void stopServer();

}
