package com.collective.celos;
import static org.apache.oozie.client.WorkflowJob.Status.FAILED;
import static org.apache.oozie.client.WorkflowJob.Status.KILLED;
import static org.apache.oozie.client.WorkflowJob.Status.PREP;
import static org.apache.oozie.client.WorkflowJob.Status.RUNNING;
import static org.apache.oozie.client.WorkflowJob.Status.SUCCEEDED;
import static org.apache.oozie.client.WorkflowJob.Status.SUSPENDED;

import org.junit.Assert;
import org.junit.Test;

public class OozieExternalStatusTest {

    @Test
    public void testIsRunning() {
        Assert.assertEquals(new OozieExternalStatus(PREP.toString()).isRunning(), true);
        Assert.assertEquals(new OozieExternalStatus(RUNNING.toString()).isRunning(), true);
        Assert.assertEquals(new OozieExternalStatus(SUSPENDED.toString()).isRunning(), false);
        Assert.assertEquals(new OozieExternalStatus(SUCCEEDED.toString()).isRunning(), false);
        Assert.assertEquals(new OozieExternalStatus(KILLED.toString()).isRunning(), false);
        Assert.assertEquals(new OozieExternalStatus(FAILED.toString()).isRunning(), false);
    }

    @Test
    public void testIsSuccess() {
        Assert.assertEquals(new OozieExternalStatus(PREP.toString()).isSuccess(), false);
        Assert.assertEquals(new OozieExternalStatus(RUNNING.toString()).isSuccess(), false);
        Assert.assertEquals(new OozieExternalStatus(SUSPENDED.toString()).isSuccess(), false);
        Assert.assertEquals(new OozieExternalStatus(SUCCEEDED.toString()).isSuccess(), true);
        Assert.assertEquals(new OozieExternalStatus(KILLED.toString()).isSuccess(), false);
        Assert.assertEquals(new OozieExternalStatus(FAILED.toString()).isSuccess(), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStatus() {
        new OozieExternalStatus("unexpected value");
    }

}
