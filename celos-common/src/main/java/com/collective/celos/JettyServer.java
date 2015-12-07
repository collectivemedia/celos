/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.security.*;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Common Jetty code for Celos server and Celos UI.
 */
public class JettyServer {

    private Server server;
    private WebAppContext context;
    private SecurityHandler securityHandler = createDefaultSecurityHandler();

    public int start() throws Exception {
        this.server = new Server();
        this.context = createWebAppContext();

        ServerConnector serverConnector = new ServerConnector(server);
        server.setConnectors(new Connector[]{serverConnector});

        securityHandler.setHandler(context);
        server.setHandler(securityHandler);
        server.start();

        return serverConnector.getLocalPort();
    }

    public int start(int port) throws Exception {
        this.server = new Server(port);
        this.context = createWebAppContext();

        securityHandler.setHandler(context);
        server.setHandler(securityHandler);
        server.start();

        return port;
    }

    protected void setSecurityHandler(SecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
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

    protected WebAppContext getContext() {
        return context;
    }

    private WebAppContext createWebAppContext() throws URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("WEB-INF");
        URIBuilder uriBuilder = new URIBuilder(url.toURI());
        uriBuilder.setPath(Paths.get(url.getPath()).getParent().toString());

        WebAppContext context = new WebAppContext(uriBuilder.toString() + "/", "/");
        context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        context.setExtractWAR(false);
        context.setServer(server);

        return context;
    }

    private static SecurityHandler createDefaultSecurityHandler() {
        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();

        Constraint constraint = new Constraint();
        constraint.setAuthenticate(false);

        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec("/*");
        mapping.setConstraint(constraint);

        securityHandler.setConstraintMappings(Collections.singletonList(mapping));

        return securityHandler;
    }

}