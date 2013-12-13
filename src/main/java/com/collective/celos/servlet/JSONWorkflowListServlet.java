package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.SchedulerConfiguration;
import com.collective.celos.Scheduler;
import com.collective.celos.Workflow;
import com.collective.celos.WorkflowConfiguration;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Returns list of configured workflows as JSON.
 */
@SuppressWarnings("serial")
public class JSONWorkflowListServlet extends AbstractJSONServlet {
    
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            Scheduler sch = new SchedulerConfiguration().makeDefaultScheduler();
            WorkflowConfiguration cfg = sch.getWorkflowConfiguration();
            ObjectNode object = createJSONObject(cfg);
            writer.writeValue(res.getOutputStream(), object);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

    ObjectNode createJSONObject(WorkflowConfiguration cfg) {
        ArrayNode list = mapper.createArrayNode();
        for (Workflow wf : cfg.getWorkflows()) {
            list.add(wf.getID().toString());
        }
        ObjectNode object = mapper.createObjectNode();
        object.put("ids", list);
        return object;
    }

}
