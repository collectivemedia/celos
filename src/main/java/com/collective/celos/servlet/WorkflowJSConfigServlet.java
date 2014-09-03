package com.collective.celos.servlet;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.CommandExternalService;
import org.apache.commons.io.FileUtils;

import com.collective.celos.WorkflowID;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;
import org.eclipse.jetty.util.StringUtil;

/**
 * GET Returns JS-config file contents for particular workflow-id
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
 * POST Saves JS-config file contents in workflow dir
 *
 * POST /workflow-file?filename=myworkflow.js
 * POST Body:
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
 */
@SuppressWarnings("serial")
public class WorkflowJSConfigServlet extends AbstractJSONServlet {

    private static final String ID_PARAM = "id";
    private static final String FILENAME_PARAM = "filename";

    public WorkflowJSConfigServlet(ServerConfig serverConfig) {
        super(serverConfig);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String id = req.getParameter(ID_PARAM);
        if (id == null) {
            throw new IllegalArgumentException(ID_PARAM + " parameter missing.");
        }
        try {
            String contents = getWorkflowConfigurationFileContents(id);
            if (contents == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "JS config for workflow not found: " + id);
            } else {
                res.getOutputStream().write(contents.getBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            String filename = req.getParameter(FILENAME_PARAM);
            String contents = IOUtils.toString(req.getInputStream());

            FileUtils.write(new File(getServerConfig().getWorkflowConfigurationPath(), filename), contents);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getWorkflowConfigurationFileContents(String workflowId) throws Exception {
        String filePath = getOrCreateCachedScheduler().getWorkflowConfiguration().getWorkflowJSFileName(new WorkflowID(workflowId));
        if (filePath == null) {
            return null;
        } else {
            return FileUtils.readFileToString(new File(filePath));
        }
    }
    
}
