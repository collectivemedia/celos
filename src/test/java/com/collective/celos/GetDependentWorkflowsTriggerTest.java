package com.collective.celos;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GetDependentWorkflowsTriggerTest {

    @Test
    public void successTriggerReturnsCorrectResult() throws Exception {
        Set<WorkflowID> expected = Sets.newHashSet(new WorkflowID("foo"));
        Assert.assertEquals(expected, new SuccessTrigger("foo").getWorkflowsTriggerDependsOn());
    }

    @Test
    public void andTriggerReturnsCorrectResult() throws Exception {
        Set<WorkflowID> expected = Sets.newHashSet(new WorkflowID("foo"), new WorkflowID("bar"));
        List<Trigger> triggers = (List) Lists.newArrayList(new SuccessTrigger("foo"), new SuccessTrigger("bar"));
        Assert.assertEquals(expected, new AndTrigger(triggers).getWorkflowsTriggerDependsOn());
    }

    @Test
    public void notTriggerReturnsCorrectResult() throws Exception {
        Set<WorkflowID> expected = Sets.newHashSet(new WorkflowID("foo"), new WorkflowID("bar"));
        List<Trigger> triggers = (List) Lists.newArrayList(new SuccessTrigger("foo"), new SuccessTrigger("bar"));
        NotTrigger notTrigger = new NotTrigger(new AndTrigger(triggers));
        Assert.assertEquals(expected, notTrigger.getWorkflowsTriggerDependsOn());
    }

    @Test
    public void complexTriggerReturnsCorrectResult() throws Exception {
        Set<WorkflowID> expected = Sets.newHashSet(new WorkflowID("foo1"), new WorkflowID("bar1"), new WorkflowID("foo2"), new WorkflowID("bar2"));

        List<Trigger> triggers1 = (List) Lists.newArrayList(new SuccessTrigger("foo1"), new SuccessTrigger("bar1"));
        List<Trigger> triggers2 = (List) Lists.newArrayList(new SuccessTrigger("foo2"), new SuccessTrigger("bar2"));
        Trigger dt = new DelayTrigger(23);
        Trigger and1 = new AndTrigger(triggers1);
        Trigger and2 = new AndTrigger(triggers2);
        CommandTrigger cm = new CommandTrigger(Collections.EMPTY_LIST);

        Trigger t1 = new AndTrigger(Lists.newArrayList(and1, dt));
        Trigger t2 = new AndTrigger(Lists.newArrayList(and2, cm, new NeverTrigger()));
        Assert.assertEquals(expected, new AndTrigger(Lists.newArrayList(t1, t2, dt)).getWorkflowsTriggerDependsOn());

    }


}
