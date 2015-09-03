package com.collective.celos.servlet;

import com.collective.celos.Scheduler;
import com.collective.celos.Workflow;
import com.collective.celos.WorkflowConfiguration;
import com.collective.celos.WorkflowID;
import com.collective.celos.trigger.AlwaysTrigger;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerStatusServletTest {

    @Test
    public void parsesIDsParameterMissing() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter(TriggerStatusServlet.ID_PARAM)).thenReturn(null);
        Assert.assertEquals(Collections.emptySet(), new SchedulerServlet().getWorkflowIDs(req));
    }
    
    @Test
    public void parsesIDsParameter() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Scheduler scheduler = mock(Scheduler.class);
        WorkflowConfiguration workflowConfiguration = mock(WorkflowConfiguration.class);
        Workflow workflow = mock(Workflow.class);

        when(request.getParameter(TriggerStatusServlet.ID_PARAM)).thenReturn("foo");
        when(request.getParameter("time")).thenReturn("2015-08-24T00:00:00Z");
        when(scheduler.getWorkflowConfiguration()).thenReturn(workflowConfiguration);
        when(workflowConfiguration.findWorkflow(new WorkflowID("foo"))).thenReturn(workflow);
        when(workflow.getTrigger()).thenReturn(new AlwaysTrigger());

        String result = new TriggerStatusServlet().getStatus(request, response, scheduler, workflow);
        Assert.assertEquals("{\n" +
                "  \"ready\" : true,\n" +
                "  \"description\" : \"Always succeed\",\n" +
                "  \"subStatuses\" : [ ]\n" +
                "}",
                result);
    }
}
