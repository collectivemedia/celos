package com.collective.celos.server;

import com.collective.celos.servlet.*;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class CelosServer {

    private Server server = new Server();

    public Integer startServer(Map<String, String> jsVariables, File workflowConfigurationPath, File defaultsConfigurationPath, File stateDatabasePath) throws Exception {

        workflowConfigurationPath.mkdirs();
        defaultsConfigurationPath.mkdirs();
        stateDatabasePath.mkdirs();

        String webAppParent = getUriParentPath(Thread.currentThread().getContextClassLoader().getResource("WEB-INF").toURI().toString());
        WebAppContext context = new WebAppContext(webAppParent.toString(), "/");

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

    private static String getUriParentPath(String oldUri) throws URIException {
        URI uri = new org.apache.commons.httpclient.URI(oldUri, false);
        Path parentPath = Paths.get(uri.getPath()).getParent();
        uri.setPath(parentPath.toString() + "/");
        return uri.toString();
    }

    public void stopServer() throws Exception {
        server.stop();
        server.destroy();
        server.join();
    }

}