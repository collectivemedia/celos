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

        createPath(workflowConfigurationPath);
        createPath(defaultsConfigurationPath);
        createPath(stateDatabasePath);

        Server server = new Server(port);

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

        server.start();
        server.join();
    }

    private void createPath(String paramPath) {
        File path = new File(paramPath);
        path.mkdirs();
    }


    public static void main(String[] args) throws Exception {
        CelosServer celosServer = new CelosServer();
        celosServer.startServer(8080, Collections.<String, String>emptyMap(), "/home/akonopko/work/celos2/delme/1", "/home/akonopko/work/celos2/delme/2", "/home/akonopko/work/celos2/delme/3");
    }
}