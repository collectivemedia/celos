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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Sets;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Renders the UI JSON.
 */
public class ConfigServlet extends HttpServlet {

    private static final String GROUPS_TAG = "groups";
    private static final String WORKFLOWS_TAG = "workflows";
    private static final String NAME_TAG = "name";
    private static final String UNLISTED_WORKFLOWS_CAPTION = "Unlisted workflows";
    private static final String DEFAULT_CAPTION = "All Workflows";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected static class WorkflowGroupRef {
        public String name;
        public List<Object> rows;
    }

    protected final static ObjectMapper mapper = new ObjectMapper();
    protected final static ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    protected static class ConfigUI {
        public List<WorkflowGroupRef> rows;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            final ServletContext servletContext = getServletContext();
            final URL celosURL = (URL) Util.requireNonNull(servletContext.getAttribute(Main.CELOS_URL_ATTR));
            final CelosClient client = new CelosClient(celosURL.toURI());
            final Path configFile = ((File) servletContext.getAttribute(Main.CONFIG_FILE_ATTR)).toPath();
            final Optional<String> config = (Files.exists(configFile))
                    ? Optional.of(new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8))
                    : Optional.empty();
            Set<WorkflowID> workflowIDs = client.getWorkflowList();
            final String tmp = processGet(workflowIDs, config);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(tmp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public static String processGet(Set<WorkflowID> workflowIDs, Optional<String> configFile) throws Exception {
        List<WorkflowGroup> groups;

        if (configFile.isPresent()) {
            groups = getWorkflowGroups(configFile.get(), workflowIDs);
        } else {
            groups = getDefaultGroups(workflowIDs);
        }
        final ConfigUI result = new ConfigUI();
        result.rows = new ArrayList<>();
        for (WorkflowGroup g : groups) {
            final WorkflowGroupRef group = new WorkflowGroupRef();
            group.name = g.getName();
            group.rows = Collections.emptyList();
            result.rows.add(group);
        }
        return writer.writeValueAsString(result);
    }

    static List<WorkflowGroup> getWorkflowGroups(String configFileIS, Set<WorkflowID> expectedWfs) throws IOException {
        JsonNode mainNode = objectMapper.readValue(configFileIS, JsonNode.class);
        List<WorkflowGroup> configWorkflowGroups = new ArrayList<>();
        Set<WorkflowID> listedWfs = new TreeSet<>();

        for(JsonNode workflowGroupNode: mainNode.get(GROUPS_TAG)) {
            String[] workflowNames = objectMapper.treeToValue(workflowGroupNode.get(WORKFLOWS_TAG), String[].class);

            List<WorkflowID> ids = new ArrayList<>();
            for (String wfName : workflowNames) {
                ids.add(new WorkflowID(wfName));
            }

            String name = workflowGroupNode.get(NAME_TAG).textValue();
            configWorkflowGroups.add(new WorkflowGroup(name, ids));
            listedWfs.addAll(ids);
        }

        TreeSet<WorkflowID> diff = new TreeSet<>(Sets.difference(expectedWfs, listedWfs));
        if (!diff.isEmpty()) {
            configWorkflowGroups.add(new WorkflowGroup(UNLISTED_WORKFLOWS_CAPTION, new ArrayList<>(diff)));
        }
        return configWorkflowGroups;
    }

    private static List<WorkflowGroup> getDefaultGroups(Set<WorkflowID> workflows) {
        return Collections.singletonList(new WorkflowGroup(DEFAULT_CAPTION, new LinkedList<>(new TreeSet<>(workflows))));
    }

}
