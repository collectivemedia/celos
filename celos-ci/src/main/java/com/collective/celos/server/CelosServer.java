package com.collective.celos.server;

import com.collective.celos.servlet.*;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.util.Map;

public class CelosServer {

    private Server server = new Server();

    public Integer startServer(Map<String, String> jsVariables, File workflowConfigurationPath, File defaultsConfigurationPath, File stateDatabasePath) throws Exception {

        workflowConfigurationPath.mkdirs();
        defaultsConfigurationPath.mkdirs();
        stateDatabasePath.mkdirs();

        String rootPath = new File(Thread.currentThread().getContextClassLoader().getResource("WEB-INF").toURI()).getParent();
        WebAppContext context = new WebAppContext(rootPath, "/");

        server.setHandler(context);

        ServerConnector connector = new ServerConnector(server);
        server.setConnectors(new Connector[]{connector});
        server.start();

        context.setAttribute(AbstractServlet.ADDITIONAL_JS_VARIABLES, jsVariables);
        context.setInitParameter(AbstractServlet.WORKFLOW_CONFIGURATION_PATH_ATTR, workflowConfigurationPath.getAbsolutePath());
        context.setInitParameter(AbstractServlet.DEFAULTS_CONFIGURATION_PATH_ATTR, defaultsConfigurationPath.getAbsolutePath());
        context.setInitParameter(AbstractServlet.STATE_DATABASE_PATH_ATTR, stateDatabasePath.getAbsolutePath());

        return connector.getLocalPort();

    }

    public void stopServer() throws Exception {
        server.stop();
        server.destroy();
        server.join();
    }

}