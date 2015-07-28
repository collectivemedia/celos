package com.collective.celos;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class JettyServer {

    private Server server;
    private WebAppContext context;

    public int start() throws Exception {
        server = new Server();
        return createServer();
    }

    public void start(int port) throws Exception {
        server = new Server(port);
        createServer();
    }

    public void stop() throws Exception {
        if (server == null) {
            throw new IllegalStateException("JettyServer was not started");
        }
        server.stop();
        server.destroy();
        server.join();
    }


    public void setupContext(Map<String, Object> attributes, Map<String, String> initParam) {
        for (Map.Entry<String, Object> entry: attributes.entrySet()) {
            context.setAttribute(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry: initParam.entrySet()) {
            context.setInitParameter(entry.getKey(), entry.getValue());
        }
    }

    private int createServer() throws Exception {
        URL url = Thread.currentThread().getContextClassLoader().getResource("WEB-INF");
        URIBuilder uriBuilder = new URIBuilder(url.toURI());
        uriBuilder.setPath(Paths.get(url.getPath()).getParent().toString());

        context = new WebAppContext(uriBuilder.toString() + "/", "/");

        server.setHandler(context);

        ServerConnector connector = new ServerConnector(server);
        server.setConnectors(new Connector[]{connector});
        server.start();

        return connector.getLocalPort();
    }


}