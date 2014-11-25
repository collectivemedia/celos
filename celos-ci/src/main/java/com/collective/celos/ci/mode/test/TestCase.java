package com.collective.celos.ci.mode.test;

import com.collective.celos.ScheduledTime;
import com.collective.celos.ci.testing.fixtures.compare.FixtureComparer;
import com.collective.celos.ci.testing.fixtures.deploy.FixtureDeployer;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by akonopko on 21.11.14.
 */
public class TestCase {

    private final String name;
    private final ScheduledTime sampleTimeStart;
    private final ScheduledTime sampleTimeEnd;
    private final List<FixtureDeployer> inputs = Lists.newArrayList();
    private final List<FixtureComparer> outputs = Lists.newArrayList();

    public TestCase(String name, String sampleTimeStart, String sampleTimeEnd) {
        this.name = name;
        this.sampleTimeStart = new ScheduledTime(sampleTimeStart);
        this.sampleTimeEnd = new ScheduledTime(sampleTimeEnd);
    }

    public void addInput(FixtureDeployer fixtureDeployer) {
        inputs.add(fixtureDeployer);
    }

    public void addOutput(FixtureComparer fixtureComparer) {
        outputs.add(fixtureComparer);
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
}
