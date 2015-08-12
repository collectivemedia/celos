package com.collective.celos.ci.mode.test;

import java.net.URI;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestRunCelosServerProvided implements TestRunCelosServer {

    private final URI celosServerUrl;

    public TestRunCelosServerProvided(URI celosServerUrl) {
        this.celosServerUrl = celosServerUrl;
    }

    public URI startServer() throws Exception {
        return celosServerUrl;
    }

    public void stopServer() {
    }

}
