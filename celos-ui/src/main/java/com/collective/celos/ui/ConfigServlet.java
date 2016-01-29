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
import com.collective.celos.pojo.WorkflowGroupPOJO;
import com.collective.celos.pojo.WorkflowPOJO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Sets;

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
import java.util.stream.Collectors;

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

    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected final static ObjectMapper mapper = new ObjectMapper();
    protected final static ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    protected static class ConfigUI {
        public List<WorkflowGroupPOJO> rows;
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
            List<String> workflowIDs = client.getWorkflowList().stream().map(WorkflowID::toString).collect(toList());
            final String tmp = processGet(workflowIDs, config);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(tmp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public static String processGet(List<String> workflowIDs, Optional<String> configFile) throws Exception {
        List<WorkflowGroupPOJO> groups;

        if (configFile.isPresent()) {
            groups = getWorkflowGroups(configFile.get(), workflowIDs);
        } else {
            groups = getDefaultGroups(workflowIDs);
        }
        final ConfigUI result = new ConfigUI();
        result.rows = groups;
        return writer.writeValueAsString(result);
    }

    static List<WorkflowGroupPOJO> getWorkflowGroups(String configFileIS, List<String> expectedWfs) throws IOException {
        JsonNode mainNode = objectMapper.readValue(configFileIS, JsonNode.class);
        List<WorkflowGroupPOJO> configWorkflowGroups = new ArrayList<>();
        Set<String> listedWfs = new TreeSet<>();

        for(JsonNode workflowGroupNode: mainNode.get(GROUPS_TAG)) {
            String[] workflowNames = objectMapper.treeToValue(workflowGroupNode.get(WORKFLOWS_TAG), String[].class);

            String name = workflowGroupNode.get(NAME_TAG).textValue();
            final List<WorkflowPOJO> collect = Arrays.stream(workflowNames)
                    .map(WorkflowPOJO::new)
                    .collect(toList());
            configWorkflowGroups.add(new WorkflowGroupPOJO(name).setRows(collect));
            listedWfs.addAll(Arrays.stream(workflowNames).collect(toSet()));
        }

        final List<WorkflowPOJO> collect = expectedWfs.stream()
                .filter(listedWfs::contains)
                .map(WorkflowPOJO::new)
                .collect(toList());
        if (!collect.isEmpty()) {
            configWorkflowGroups.add(new WorkflowGroupPOJO(UNLISTED_WORKFLOWS_CAPTION)
                                        .setRows(collect)
            );
        }
        return configWorkflowGroups;
    }

    private static List<WorkflowGroupPOJO> getDefaultGroups(List<String> workflows) {
        return Collections.singletonList(
                new WorkflowGroupPOJO(DEFAULT_CAPTION)
                        .setRows(workflows.stream()
                                .map(WorkflowPOJO::new)
                                .collect(toList())
                        )
        );
    }

}
