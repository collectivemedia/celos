package com.collective.celos.servlet;

import com.collective.celos.servlet.*;
import com.google.common.collect.Maps;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.eclipse.jetty.util.log.StdErrLog;

import java.util.Collections;
import java.util.Map;

public class CelosServer {


    public CelosServer() {
    }

    public void startServer(int port, Map<String, String> jsVariables) throws Exception {
        org.eclipse.jetty.util.log.Log.setLog(new StdErrLog());

        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(SchedulerServlet.class, "/scheduler");
        context.addServlet(JSONWorkflowListServlet.class, "/workflow-list");
        context.addServlet(JSONWorkflowServlet.class, "/workflow");
        context.addServlet(JSONSlotStateServlet.class, "/slot-state");
        context.addServlet(RerunServlet.class, "/rerun");
        context.addServlet(WorkflowJSConfigServlet.class, "/workflow-file");
        context.addServlet(ClearCacheServlet.class, "/clear-cache");

        server.start();
        server.join();
    }


    public static void main(String[] args) throws Exception {
        CelosServer celosServer = new CelosServer();
        celosServer.startServer(8080, Collections.<String, String>emptyMap());
    }
}