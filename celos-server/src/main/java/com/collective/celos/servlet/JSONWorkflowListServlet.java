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
package com.collective.celos.servlet;

import com.collective.celos.Scheduler;
import com.collective.celos.Workflow;
import com.collective.celos.WorkflowConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.*;

/**
 * Returns list of IDs of configured workflows as JSON.
 *
 * GET /workflow-list
 * ==
 * {
 *   "ids": [ "workflow-1", "workflow-2" ]
 * }
 */
@SuppressWarnings("serial")
public class JSONWorkflowListServlet extends AbstractJSONServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            List<ArrayNode> arrayNodeList = getSlaveWorkflowListsAsync(req);

            Scheduler sch = getOrCreateCachedScheduler();
            WorkflowConfiguration cfg = sch.getWorkflowConfiguration();
            ObjectNode object = createJSONObject(cfg, arrayNodeList);
            writer.writeValue(res.getOutputStream(), object);

        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

    private List<ArrayNode> getSlaveWorkflowListsAsync(final HttpServletRequest req) throws InterruptedException, ExecutionException {
        List<ArrayNode> arrayNodeList = Lists.newArrayList();

        if (getSwarmPorts().size() > 0) {
            int numberOfTasks = getSwarmPorts().size() - 1;
            ExecutorService executor = Executors.newFixedThreadPool(numberOfTasks);
            CompletionService<ArrayNode> completionService = new ExecutorCompletionService<ArrayNode>(executor);

            for(Integer port : getSwarmPorts()) {
                if (port != req.getServerPort()) {
                    Callable<ArrayNode> callable = new Callable<ArrayNode>() {
                        @Override
                        public ArrayNode call() throws Exception {
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpGet get = new HttpGet("http://localhost:" + port + req.getRequestURI());
                            HttpResponse response = httpClient.execute(get);
                            JsonNode jsonNode = mapper.readTree(response.getEntity().getContent());
                            return (ArrayNode) jsonNode.get("ids");
                        }
                    };
                    completionService.submit(callable);
                }
            }

            int received = 0;
            while(received < numberOfTasks) {
                Future<ArrayNode> arrayNodeFuture = completionService.take(); //blocks if none available
                arrayNodeList.add(arrayNodeFuture.get());
                received++;
            }
            executor.shutdown();
        }
        return arrayNodeList;
    }

    ObjectNode createJSONObject(WorkflowConfiguration cfg, List<ArrayNode> arrayNodeList) {
        // Make sure the IDs are sorted
        SortedSet<String> ids = new TreeSet<String>();
        for (Workflow wf : cfg.getWorkflows()) {
            ids.add(wf.getID().toString());
        }
        for (ArrayNode arrayNode : arrayNodeList) {
            for (JsonNode node : arrayNode) {
                ids.add(node.textValue());
            }
        }
        ArrayNode list = mapper.createArrayNode();
        for (String id : ids) {
            list.add(id);
        }
        ObjectNode object = mapper.createObjectNode();
        object.put("ids", list);
        return object;
    }

}
