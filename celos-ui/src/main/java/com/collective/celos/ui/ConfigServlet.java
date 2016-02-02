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
package com.collective.celos.ui;

import com.collective.celos.*;
import com.collective.celos.pojo.Config;
import com.collective.celos.pojo.WorkflowGroup;
import com.collective.celos.pojo.Workflow;
import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Renders the UI JSON.
 */
public class ConfigServlet extends HttpServlet {

    private static final String GROUPS_TAG = "groups";
    private static final String WORKFLOWS_TAG = "workflows";
    private static final String NAME_TAG = "name";
    private static final String UNLISTED_WORKFLOWS_CAPTION = "Unlisted workflows";
    private static final String DEFAULT_CAPTION = "All Workflows";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            final ServletContext servletContext = getServletContext();
            CelosClient client = Main.getCelosClient(servletContext);
            List<String> workflowIDs = client.getWorkflowList().stream().map(WorkflowID::toString).collect(toList());
            final Optional<String> celosConfig = Main.getCelosConfig(servletContext);
            final String tmp = processGet(workflowIDs, celosConfig);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(tmp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public String processGet(List<String> workflowIDs, Optional<String> configFile) throws Exception {
        List<WorkflowGroup> groups;

        if (configFile.isPresent()) {
            groups = WorkflowsServlet.getWorkflowGroups(configFile.get(), workflowIDs);
        } else {
            groups = WorkflowsServlet.getDefaultGroups(workflowIDs);
        }
        final Config result = new Config(groups);
        return Util.JSON_PRETTY.writeValueAsString(result);
    }



}
