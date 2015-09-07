package com.collective.celos.ui;

import java.util.HashMap;
import java.util.Map;

import com.collective.celos.JettyServer;

/**
 * Main class that launches the Celos UI.
 */
public class Main {
    
    public static String CELOS_URL_ATTR = "CELOS_URL";
    public static String HUE_URL_ATTR = "HUE_URL";

    public static void main(String... args) throws Exception {
        UICommandLineParser UICommandLineParser = new UICommandLineParser();
        UICommandLine commandLine = UICommandLineParser.parse(args);
        JettyServer jettyServer = new JettyServer();
        jettyServer.start(commandLine.getPort());
        jettyServer.setupContext(getAttributes(commandLine), new HashMap<String, String>());
    }

    private static Map<String, Object> getAttributes(UICommandLine commandLine) {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put(CELOS_URL_ATTR, commandLine.getCelosUrl());
        attrs.put(HUE_URL_ATTR, commandLine.getHueUrl());
        return attrs;
    }
    
}
