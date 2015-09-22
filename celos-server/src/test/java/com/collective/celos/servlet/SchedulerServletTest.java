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

import com.collective.celos.CelosClient;
import com.collective.celos.WorkflowID;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SchedulerServletTest {

    @Test
    public void parsesIDsParameterMissing() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter(CelosClient.IDS_PARAM)).thenReturn(null);
        Assert.assertEquals(Collections.emptySet(), new SchedulerServlet().getWorkflowIDs(req));
    }
    
    @Test
    public void parsesIDsParameter() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter(CelosClient.IDS_PARAM)).thenReturn("foo,bar");
        Set<WorkflowID> ids = new HashSet<>();
        ids.add(new WorkflowID("foo"));
        ids.add(new WorkflowID("bar"));
        Assert.assertEquals(ids, new SchedulerServlet().getWorkflowIDs(req));
    }
    
}
