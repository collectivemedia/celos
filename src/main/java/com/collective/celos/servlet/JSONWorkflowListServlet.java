package com.collective.celos.servlet;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.Scheduler;
import com.collective.celos.Workflow;
import com.collective.celos.WorkflowConfiguration;
import com.collective.celos.server.ServerConfig;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Returns list of IDs of configured workflows as JSON.
 *
 * GET /workflow-list
 * ==>
 * {
 *   "ids": [ "workflow-1", "workflow-2" ]
 * }
 */
@SuppressWarnings("serial")
public class JSONWorkflowListServlet extends AbstractJSONServlet {

    public JSONWorkflowListServlet(ServerConfig celosServer) {
        super(celosServer);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            Scheduler sch = getOrCreateCachedScheduler();
            WorkflowConfiguration cfg = sch.getWorkflowConfiguration();
            ObjectNode object = createJSONObject(cfg);
            writer.writeValue(res.getOutputStream(), object);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    ObjectNode createJSONObject(WorkflowConfiguration cfg) {
        // Make sure the IDs are sorted
        SortedSet<String> ids = new TreeSet<String>();
        for (Workflow wf : cfg.getWorkflows()) {
            ids.add(wf.getID().toString());
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
