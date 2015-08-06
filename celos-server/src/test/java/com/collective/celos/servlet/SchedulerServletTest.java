package com.collective.celos.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.WorkflowID;

public class SchedulerServletTest {

    @Test
    public void parsesIDsParameterMissing() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter(SchedulerServlet.IDS_PARAM)).thenReturn(null);
        Assert.assertEquals(Collections.emptySet(), new SchedulerServlet().getWorkflowIDs(req));
    }
    
    @Test
    public void parsesIDsParameter() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter(SchedulerServlet.IDS_PARAM)).thenReturn("foo,bar");
        Set<WorkflowID> ids = new HashSet<>();
        ids.add(new WorkflowID("foo"));
        ids.add(new WorkflowID("bar"));
        Assert.assertEquals(ids, new SchedulerServlet().getWorkflowIDs(req));
    }
    
}
