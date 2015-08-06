package com.collective.celos.server;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.rolling.RollingFileAppender;
import org.apache.log4j.rolling.TimeBasedRollingPolicy;

import java.io.File;
import java.util.Collections;

public class Main {

    public static void main(String... args) throws Exception {
        ServerCommandLineParser serverCommandLineParser = new ServerCommandLineParser();
        ServerCommandLine commandLine = serverCommandLineParser.parse(args);
        CelosServer celosServer = new CelosServer();
        celosServer.startServer(commandLine.getPort(),
                Collections.<String, String>emptyMap(),
                commandLine.getWorkflowsDir(),
                commandLine.getDefaultsDir(),
                commandLine.getStateDatabase(),
                commandLine.getUiDir());

        setupLogging(commandLine.getLogDir());
    }

    private static void setupLogging(File logDir) {
        System.getProperties().setProperty("log4j.defaultInitOverride", "true");

        RollingFileAppender appender = new RollingFileAppender();
        appender.setFile(new File(logDir, "celos.log").getAbsolutePath());
        appender.setAppend(true);

        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
        rollingPolicy.setFileNamePattern(new File(logDir, "celos-%d{yyyy-MM-dd}.log").getAbsolutePath());
        appender.setRollingPolicy(rollingPolicy);

        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setConversionPattern("[%d{YYYY-MM-dd HH:mm:ss.SSS}] %-5p: %m%n");
        appender.setLayout(patternLayout);

        appender.activateOptions();
        Logger.getRootLogger().addAppender(appender);
        Logger.getRootLogger().setLevel(Level.INFO);
    }

}