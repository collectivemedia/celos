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

    public Integer startServer(Map<String, String> jsVariables, String workflowConfigurationPath, String defaultsConfigurationPath, String stateDatabasePath) throws Exception {

        assureDirIsCreated(workflowConfigurationPath);
        assureDirIsCreated(defaultsConfigurationPath);
        assureDirIsCreated(stateDatabasePath);

        String rootPath = getClass().getClassLoader().getResource(".").toString();
        WebAppContext context = new WebAppContext(rootPath, "/");

        server.setHandler(context);

        ServerConnector connector = new ServerConnector(server);
        server.setConnectors(new Connector[]{connector});
        server.start();

        context.setAttribute(AbstractServlet.ADDITIONAL_JS_VARIABLES, jsVariables);
        context.setInitParameter(AbstractServlet.WORKFLOW_CONFIGURATION_PATH_ATTR, workflowConfigurationPath);
        context.setInitParameter(AbstractServlet.DEFAULTS_CONFIGURATION_PATH_ATTR, defaultsConfigurationPath);
        context.setInitParameter(AbstractServlet.STATE_DATABASE_PATH_ATTR, stateDatabasePath);

        return connector.getLocalPort();

    }

    public void stopServer() throws Exception {
        server.stop();
        server.destroy();
        server.join();
    }

    private void assureDirIsCreated(String paramPath) {
        File path = new File(paramPath);
        path.mkdirs();
    }

}