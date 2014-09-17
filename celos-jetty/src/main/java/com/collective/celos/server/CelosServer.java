package com.collective.celos.server;

import com.collective.celos.servlet.*;
import com.google.common.collect.ImmutableMap;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.File;
import java.util.Map;

public class CelosServer {

    private Server server;

    public Integer startServer(Map<String, String> jsVariables, String workflowConfigurationPath, String defaultsConfigurationPath, String stateDatabasePath) throws Exception {
        server = new Server();

        setupContext(jsVariables, workflowConfigurationPath, defaultsConfigurationPath, stateDatabasePath);

        ServerConnector connector = new ServerConnector(server);
        server.setConnectors(new Connector[] { connector });
        server.start();

        return connector.getLocalPort();
    }

    public void startServer(int port, Map<String, String> jsVariables, String workflowConfigurationPath, String defaultsConfigurationPath, String stateDatabasePath) throws Exception {

        server = new Server(port);

        setupContext(jsVariables, workflowConfigurationPath, defaultsConfigurationPath, stateDatabasePath);

        server.start();

    }

    private void setupContext(Map<String, String> jsVariables, String workflowConfigurationPath, String defaultsConfigurationPath, String stateDatabasePath) {
        assureDirIsCreated(workflowConfigurationPath);
        assureDirIsCreated(defaultsConfigurationPath);
        assureDirIsCreated(stateDatabasePath);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new SchedulerServlet()), "/scheduler");
        context.addServlet(new ServletHolder(new JSONWorkflowListServlet()), "/workflow-list");
        context.addServlet(new ServletHolder(new JSONWorkflowServlet()), "/workflow");
        context.addServlet(new ServletHolder(new JSONSlotStateServlet()), "/slot-state");
        context.addServlet(new ServletHolder(new RerunServlet()), "/rerun");
        context.addServlet(new ServletHolder(new WorkflowJSConfigServlet()), "/workflow-file");
        context.addServlet(new ServletHolder(new ClearCacheServlet()), "/clear-cache");

        context.setInitParameter(AbstractServlet.WORKFLOW_CONFIGURATION_PATH_ATTR, workflowConfigurationPath);
        context.setInitParameter(AbstractServlet.DEFAULTS_CONFIGURATION_PATH_ATTR, defaultsConfigurationPath);
        context.setInitParameter(AbstractServlet.STATE_DATABASE_PATH_ATTR, stateDatabasePath);
        context.setAttribute(AbstractServlet.ADDITIONAL_JS_VARIABLES, jsVariables);
    }

    public void stopServer() throws Exception {
        server.stop();
        //server.join();
    }

    private void assureDirIsCreated(String paramPath) {
        File path = new File(paramPath);
        path.mkdirs();
    }

}