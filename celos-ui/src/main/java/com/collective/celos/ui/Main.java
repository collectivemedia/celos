package com.collective.celos.ui;

import com.collective.celos.JettyServer;

/**
 * Created by akonopko on 24.07.15.
 */
public class Main {

    public static void main(String... args) throws Exception {
        UIContextParser UIContextParser = new UIContextParser();
        UICommandLine commandLine = UIContextParser.parse(args);
        JettyServer jettyServer = new JettyServer();
        int port = jettyServer.start(commandLine.getPort());

        System.out.println("Hello World Celos UI is started at    localhost:" + port);
    }
}
