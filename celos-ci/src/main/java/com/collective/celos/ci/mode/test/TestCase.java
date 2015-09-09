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
package com.collective.celos.ci.mode.test;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Util;
import com.collective.celos.WorkflowID;
import com.collective.celos.ci.testing.fixtures.compare.FixtureComparer;
import com.collective.celos.ci.testing.fixtures.deploy.FixtureDeployer;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * Created by akonopko on 21.11.14.
 */
public class TestCase {

    private final String name;
    private final ScheduledTime sampleTimeStart;
    private final ScheduledTime sampleTimeEnd;
    private final List<FixtureDeployer> inputs = Lists.newArrayList();
    private final List<FixtureComparer> outputs = Lists.newArrayList();
    private final Set<WorkflowID> targetWorkflowIDs = Sets.newHashSet();

    public TestCase(String name, String sampleTimeStart, String sampleTimeEnd) {
        this(name, new ScheduledTime(sampleTimeStart), new ScheduledTime(sampleTimeEnd));
    }

    public TestCase(String name, ScheduledTime sampleTimeStart, ScheduledTime sampleTimeEnd) {
        this.name = name;
        this.sampleTimeStart = Util.requireNonNull(sampleTimeStart);
        this.sampleTimeEnd = Util.requireNonNull(sampleTimeEnd);
    }

    public void addInput(FixtureDeployer fixtureDeployer) {
        inputs.add(fixtureDeployer);
    }

    public void addOutput(FixtureComparer fixtureComparer) {
        outputs.add(fixtureComparer);
    }

    public void addTargetWorkflow(WorkflowID workflowID) {
        this.targetWorkflowIDs.add(workflowID);
    }

    public String getName() {
        return name;
    }

    public ScheduledTime getSampleTimeStart() {
        return sampleTimeStart;
    }

    public ScheduledTime getSampleTimeEnd() {
        return sampleTimeEnd;
    }

    public List<FixtureDeployer> getInputs() {
        return inputs;
    }

    public List<FixtureComparer> getOutputs() {
        return outputs;
    }

    public Set<WorkflowID> getTargetWorkflows() {
        return targetWorkflowIDs;
    }
}
