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
package com.collective.celos.ui;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class ReactServletTest {

    @Test
    public void testWorkflowGroup() throws IOException {
        final ReactWorkflowsServlet.WorkflowGroupPOJO xx = new ReactWorkflowsServlet.WorkflowGroupPOJO();
        xx.name = "groupName";
        xx.times = new ArrayList<>();
        xx.times.add("0000");
        xx.times.add("0001");
        xx.times.add("0002");
        xx.times.add("0003");
        xx.times.add("0004");
        xx.times.add("0005");
        xx.rows = new ArrayList<>();
        xx.rows.add(new ReactWorkflowsServlet.WorkflowPOJO());
        xx.rows.get(0).workflowName = "wf 1";
        xx.rows.get(0).slots = new ArrayList<>();
        xx.rows.get(0).slots.add(new ReactWorkflowsServlet.SlotPOJO());
        xx.rows.get(0).slots.get(0).status = "ready";
        xx.rows.get(0).slots.add(new ReactWorkflowsServlet.SlotPOJO());
        xx.rows.get(0).slots.get(1).status = "wait";
        xx.rows.add(new ReactWorkflowsServlet.WorkflowPOJO());
        xx.rows.get(1).workflowName = "wf 1";
        xx.rows.get(1).slots = new ArrayList<>();
        xx.rows.get(1).slots.add(new ReactWorkflowsServlet.SlotPOJO());
        xx.rows.get(1).slots.get(0).status = "ready";
        xx.rows.get(1).slots.add(new ReactWorkflowsServlet.SlotPOJO());
        xx.rows.get(1).slots.get(1).status = "wait";
    }

    @Test
    public void testMain() throws IOException {
        final ReactMainServlet.MainUI zz = new ReactMainServlet.MainUI();
        zz.currentTime = "2015-09-15 21:50 UTC";
        zz.rows = new ArrayList<>();
        zz.rows.add(new ReactMainServlet.WorkflowGroupRef());
        zz.rows.get(0).name = "dsada";
        zz.rows.add(new ReactMainServlet.WorkflowGroupRef());
        zz.rows.get(1).name = "Gr 2";
    }

}
