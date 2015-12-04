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
package com.collective.celos.server;

import com.collective.celos.Constants;
import com.collective.celos.JettyServer;
import com.collective.celos.Scheduler;
import com.collective.celos.Util;
import com.collective.celos.servlet.AbstractServlet;
import com.google.common.collect.ImmutableMap;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.DigestAuthenticator;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * HTTP server wrapping a scheduler and providing the HTTP API.
 */
public class CelosServer extends JettyServer {

    public static final String[] DIGEST_ROLES = new String[]{"user", "admin"};
    public static final String CONSTRAINT_NAME = "auth";

    public CelosServer() throws Exception {}

    public int start(Map<String, String> jsVariables, File workflowsDir, File defaultsDir, File stateDatabase) throws Exception {
        int port = super.start();
        setupContext(jsVariables, workflowsDir, defaultsDir,stateDatabase);
        return port;
    }

    public int start(int port, Map<String, String> jsVariables, File workflowsDir, File defaultsDir, File stateDatabase) throws Exception {
        super.start(port);
        setupContext(jsVariables, workflowsDir, defaultsDir, stateDatabase);
        return port;
    }

    private void setupContext(Map<String, String> jsVariables, File workflowsDir, File defaultsDir, File stateDatabase) {
        validateDirExists(workflowsDir);
        validateDirExists(defaultsDir);
        validateDirExists(stateDatabase);

        Map<String, Object> attributes = ImmutableMap.<String, Object>of(Constants.ADDITIONAL_JS_VARIABLES, jsVariables);
        Map<String, String> initParams = ImmutableMap.of(Constants.WORKFLOW_CONFIGURATION_PATH_ATTR, workflowsDir.getAbsolutePath(),
                Constants.DEFAULTS_CONFIGURATION_PATH_ATTR, defaultsDir.getAbsolutePath(),
                Constants.STATE_DATABASE_PATH_ATTR, stateDatabase.getAbsolutePath());

        setupContext(attributes, initParams);
    }

    private void validateDirExists(File dir) {
        if (dir == null || !dir.isDirectory() || !dir.exists()) {
            throw new IllegalStateException("Cannot start server: " + dir + " doesnt exist");
        }
    }

    public Scheduler getScheduler() {
        return (Scheduler) getContext().getServletHandler().getServletContext().getAttribute(AbstractServlet.SCHEDULER_ATTR);
    }

    public void setupDigestSecurity(File digestConfig) {
        Util.requireNonNull(digestConfig);
        HashLoginService loginService = new HashLoginService();
        loginService.setConfig(digestConfig.getAbsolutePath());

        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();

        Constraint constraint = new Constraint();
        constraint.setName(CONSTRAINT_NAME);
        constraint.setAuthenticate(true);
        constraint.setRoles(DIGEST_ROLES);

        ConstraintMapping getMapping = new ConstraintMapping();
        getMapping.setPathSpec("/*");
        getMapping.setMethod("GET");
        getMapping.setConstraint(new Constraint());

        ConstraintMapping putMapping = new ConstraintMapping();
        putMapping.setPathSpec("/*");
        putMapping.setMethod("PUT");
        putMapping.setConstraint(constraint);

        ConstraintMapping postMapping = new ConstraintMapping();
        postMapping.setPathSpec("/*");
        postMapping.setMethod("POST");
        postMapping.setConstraint(constraint);

        ConstraintMapping deleteMapping = new ConstraintMapping();
        deleteMapping.setPathSpec("/*");
        deleteMapping.setMethod("DELETE");
        deleteMapping.setConstraint(constraint);

        securityHandler.setConstraintMappings(Arrays.asList(getMapping, putMapping, postMapping, deleteMapping));
        securityHandler.setAuthenticator(new DigestAuthenticator());
        securityHandler.setLoginService(loginService);

        setSecurityHandler(securityHandler);
    }


}