package com.collective.celos;

import com.collective.celos.server.CelosSwarm;
import junit.framework.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akonopko on 21.10.15.
 */
public class CelosSwarmTest {

    @Test
    public void getCelosForWorkflowMoreWorkflows() throws MalformedURLException {

        WorkflowID wf1 = new WorkflowID("workflow-1");
        WorkflowID wf2 = new WorkflowID("workflow-2");
        WorkflowID wf3 = new WorkflowID("workflow-3");
        WorkflowID wf4 = new WorkflowID("workflow-4");
        WorkflowID wf5 = new WorkflowID("workflow-5");
        WorkflowID wf6 = new WorkflowID("workflow-6");
        WorkflowID wf7 = new WorkflowID("workflow-7");
        WorkflowID wf8 = new WorkflowID("workflow-8");

        URL url1 = new URL("http://url1");
        URL url2 = new URL("http://url2");
        URL url3 = new URL("http://url3");

        List<URL> celoses = new ArrayList<>();
        celoses.add(url1);
        celoses.add(url2);
        celoses.add(url3);

        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf1, celoses), url2);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf2, celoses), url1);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf3, celoses), url3);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf4, celoses), url2);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf5, celoses), url1);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf6, celoses), url3);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf7, celoses), url2);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf8, celoses), url1);
    }

    @Test
    public void getCelosForWorkflowMoreCeloses() throws MalformedURLException {

        WorkflowID wf1 = new WorkflowID("workflow-1");
        WorkflowID wf2 = new WorkflowID("workflow-2");
        WorkflowID wf3 = new WorkflowID("workflow-3");
        WorkflowID wf4 = new WorkflowID("workflow-4");
        WorkflowID wf5 = new WorkflowID("workflow-5");
        WorkflowID wf6 = new WorkflowID("workflow-6");

        URL url1 = new URL("http://url1");
        URL url2 = new URL("http://url2");
        URL url3 = new URL("http://url3");
        URL url4 = new URL("http://url4");
        URL url5 = new URL("http://url5");
        URL url6 = new URL("http://url6");
        URL url7 = new URL("http://url7");
        URL url8 = new URL("http://url8");

        List<URL> celoses = new ArrayList<>();
        celoses.add(url1);
        celoses.add(url2);
        celoses.add(url3);
        celoses.add(url4);
        celoses.add(url5);
        celoses.add(url6);
        celoses.add(url7);
        celoses.add(url8);

        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf1, celoses), url6);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf2, celoses), url5);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf3, celoses), url4);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf4, celoses), url3);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf5, celoses), url2);
        Assert.assertEquals(new CelosSwarm().getCelosForWorkflow(wf6, celoses), url1);

    }
}
