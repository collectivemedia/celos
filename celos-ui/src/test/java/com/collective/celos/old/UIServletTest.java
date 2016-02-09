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
package com.collective.celos.old;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.collective.celos.pojo.Workflow;
import com.collective.celos.pojo.WorkflowGroup;
import com.google.common.collect.*;
import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.collective.celos.WorkflowInfo;
import com.collective.celos.WorkflowStatus;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;

import static com.collective.celos.ui.UICommon.getWorkflowGroups;

public class UIServletTest {

    final String FOO = "FOO";

    @Test
    public void testRender() throws Exception {
        ScheduledTime end = new ScheduledTime("2015-09-03T13:17Z");
        ScheduledTime start = new ScheduledTime("2015-09-03T13:11Z");
        NavigableSet<ScheduledTime> tileTimes = new TreeSet<>(ImmutableSet.of(new ScheduledTime("2015-09-03T13:10Z"), new ScheduledTime("2015-09-03T13:15Z")));
        List<WorkflowGroup> groups = ImmutableList.of(new WorkflowGroup("All workflows").withRows(ImmutableList.of(new Workflow(FOO))));
        WorkflowInfo workflowInfo = new WorkflowInfo(new URL("http://example.com"), ImmutableList.of());
        WorkflowID id = new WorkflowID(FOO);
        SlotState state1 = new SlotState(new SlotID(id, new ScheduledTime("2015-09-03T13:16Z")), SlotState.Status.FAILURE);
        SlotState state2 = new SlotState(new SlotID(id, new ScheduledTime("2015-09-03T13:12Z")), SlotState.Status.WAITING);
        List<SlotState> slotStates = ImmutableList.of(state1, state2);
        Map<Workflow, WorkflowStatus> statuses = ImmutableMap.of(new Workflow(FOO), new WorkflowStatus(workflowInfo, slotStates, false));
        UIConfiguration conf = new UIConfiguration(start, end, tileTimes, groups, statuses, new URL("http://example.com"));
        
        StringWebResponse response = new StringWebResponse(UIServlet.render(conf), new URL("http://example.com"));
        WebClient webClient = new WebClient();
        webClient.setThrowExceptionOnFailingStatusCode(false);
        HtmlPage page = HTMLParser.parse(response, new TopLevelWindow("top", webClient));
        
        // Some basic sanity checking
        
        List<HtmlTableDataCell> slotCells = (List<HtmlTableDataCell>) page.getByXPath("//td[contains(@class, 'slot')]");
        Assert.assertEquals("fail", slotCells.get(0).getTextContent());
        Assert.assertEquals("wait", slotCells.get(1).getTextContent());
        
        List<HtmlTableDataCell> hourCells = (List<HtmlTableDataCell>) page.getByXPath("//td[contains(@class, 'hour')]");
        Assert.assertEquals("1315", hourCells.get(0).getTextContent());
        Assert.assertEquals("1310", hourCells.get(1).getTextContent());
        
        List<HtmlTableDataCell> workflowCells = (List<HtmlTableDataCell>) page.getByXPath("//td[@class='workflow']");
        Assert.assertEquals(FOO, workflowCells.get(0).getTextContent());
        
        System.out.println(response.getContentAsString());
    }

}
