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

    public Integer startServer(Map<String, String> jsVariables, File workflowsDir, File defaultsDir, File stateDatabase, File configDirPath) throws Exception {

        validateDirExists(workflowsDir);
        validateDirExists(defaultsDir);
        validateDirExists(stateDatabase);
        validateDirExists(configDirPath);

        String webAppParent = getUriParentPath(Thread.currentThread().getContextClassLoader().getResource("WEB-INF").toURI().toString());
        WebAppContext context = new WebAppContext(webAppParent.toString(), "/");

        server.setHandler(context);

        ServerConnector connector = new ServerConnector(server);
        server.setConnectors(new Connector[]{connector});
        server.start();

        context.setAttribute(AbstractServlet.ADDITIONAL_JS_VARIABLES, jsVariables);
        context.setInitParameter(AbstractServlet.WORKFLOW_CONFIGURATION_PATH_ATTR, workflowsDir.getAbsolutePath());
        context.setInitParameter(AbstractServlet.DEFAULTS_CONFIGURATION_PATH_ATTR, defaultsDir.getAbsolutePath());
        context.setInitParameter(AbstractServlet.STATE_DATABASE_PATH_ATTR, stateDatabase.getAbsolutePath());
        context.setInitParameter(AbstractServlet.CONFIGURATION_PATH_ATTR, configDirPath.getAbsolutePath());

        return connector.getLocalPort();

    }

    private void validateDirExists(File dir) {
        if (!dir.isDirectory() || !dir.exists()) {
            throw new IllegalStateException("Cannot start server: " + dir + " doesnt exist");
        }
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