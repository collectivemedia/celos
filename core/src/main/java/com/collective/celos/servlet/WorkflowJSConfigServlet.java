package com.collective.celos.servlet;

import com.collective.celos.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Returns information about the slot states of a single workflow as JSON.
 * 
 * GET /workflow?id=workflow-1
 * ==>
 * {
 *   "slots": {
 *     "2013-12-07T13:00:00.000Z": { "status": "RUNNING", "externalID": "237982137-371832798321-W", retryCount: 5 },
 *     "2013-12-07T14:00:00.000Z": { "status": "READY", "externalID": null, retryCount: 0 },
 *     ...
 *   }
 * }
 * 
 * If the "time" parameter is supplied, information is returned about 
 * slot states up to that time.
 */
@SuppressWarnings("serial")
public class WorkflowJSConfigServlet extends AbstractJSONServlet {

    private static final String ID_PARAM = "id";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String id = req.getParameter(ID_PARAM);
        if (id == null) {
            throw new IllegalArgumentException(ID_PARAM + " parameter missing.");
        }
        try {
            String contents = new SchedulerConfiguration().getWorkflowConfigurationFileContents(id);

            if (contents == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "JS config for workflow not found: " + id);
            } else {
                res.getOutputStream().write(contents.getBytes());
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}
