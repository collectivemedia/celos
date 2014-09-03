package com.collective.celos.server;

import com.collective.celos.servlet.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class CelosServer {

    public void startServer(int port, Map<String, String> jsVariables,
                            String workflowConfigurationPath, String defaultsConfigurationPath,
                            String stateDatabasePath) throws Exception {

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setWorkflowConfigurationPath(workflowConfigurationPath);
        serverConfig.setDefaultsConfigurationPath(defaultsConfigurationPath);
        serverConfig.setStateDatabasePath(stateDatabasePath);

        createPath(workflowConfigurationPath);
        createPath(defaultsConfigurationPath);
        createPath(stateDatabasePath);

        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new SchedulerServlet(serverConfig)), "/scheduler");
        context.addServlet(new ServletHolder(new JSONWorkflowListServlet(serverConfig)), "/workflow-list");
        context.addServlet(new ServletHolder(new JSONWorkflowServlet(serverConfig)), "/workflow");
        context.addServlet(new ServletHolder(new JSONSlotStateServlet(serverConfig)), "/slot-state");
        context.addServlet(new ServletHolder(new RerunServlet(serverConfig)), "/rerun");
        context.addServlet(new ServletHolder(new WorkflowJSConfigServlet(serverConfig)), "/workflow-file");
        context.addServlet(new ServletHolder(new ClearCacheServlet(serverConfig)), "/clear-cache");

        server.start();
        server.join();
    }

    private void createPath(String paramPath) {
        File path = new File(paramPath);
        path.mkdirs();
    }


    public static void main(String[] args) throws Exception {
        CelosServer celosServer = new CelosServer();
        celosServer.startServer(8080, Collections.<String, String>emptyMap(), ServerConfig.WORKFLOW_CONFIGURATION_PATH, ServerConfig.DEFAULTS_CONFIGURATION_PATH, ServerConfig.STATE_DATABASE_PATH);
    }
}