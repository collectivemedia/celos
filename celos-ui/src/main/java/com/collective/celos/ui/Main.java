package com.collective.celos.ui;

import com.collective.celos.JettyServer;

/**
 * Created by akonopko on 24.07.15.
 */
public class Main {

    public static void main(String... args) throws Exception {
        JettyServer jettyServer = new JettyServer();
        int port = jettyServer.start();

        System.out.println("Hello World Celos UI is started at    localhost:" + port);
    }
}
