package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.SchedulerConfiguration;

/**
 * Returns JS-config file contents for particular workflow-id
 * 
 * GET /workflow-file?id=workflow-1
 * ==>
 *
 *  addWorkflow({
 *     "id": "workflow-1",
 *      "schedule": hourlySchedule(),
 *      "schedulingStrategy": serialSchedulingStrategy(),
 *      "trigger": hdfsCheckTrigger("foo", "file:///"),
 *      "externalService": oozieExternalService({}, "oj01/oozie"),
 *      "maxRetryCount": 0
 *  });
 *
 *
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
