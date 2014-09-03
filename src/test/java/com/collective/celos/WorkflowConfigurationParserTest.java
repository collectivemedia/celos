package com.collective.celos;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Test;

public class WorkflowConfigurationParserTest {

    @Test
    public void emptyDirCreatesEmptyWorkflowConfiguration() throws Exception {
        WorkflowConfiguration cfg = parseDir("empty");
        Assert.assertEquals(0, cfg.getWorkflows().size());
    }

    @Test
    public void failsOnIllFormattedFile() throws Exception {
        expectMessage("ill-formatted", "missing ; before statement");
    }

    public static class TestSchedule implements Schedule {
        @Override
        public SortedSet<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end) {
            return null;
        }
    }
    
    public static class TestSchedulingStrategy implements SchedulingStrategy {
        @Override
        public List<SlotState> getSchedulingCandidates(List<SlotState> states) {
            return null;
        }
    }

    public static class TestExternalService implements ExternalService {
        @Override
        public String submit(SlotID id) throws ExternalServiceException {
            return null;
        }
        @Override
        public void start(SlotID id, String externalID) throws ExternalServiceException {
        }
        @Override
        public ExternalStatus getStatus(SlotID slotId, String externalWorkflowID) throws ExternalServiceException {
            return null;
        }
    }
        
    public static class TestTrigger implements Trigger {
        @Override
        public boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime t) throws Exception {
            return false;
        }
    }

    @Test
    public void propertiesAreCorrectlySet() throws Exception {
        WorkflowConfiguration cfg = parseFile("properties-test");
        
        Workflow wf1 = cfg.findWorkflow(new WorkflowID("workflow-1"));
        Assert.assertEquals("workflow-1", wf1.getID().toString());
        Assert.assertEquals(55, wf1.getMaxRetryCount());
        Assert.assertEquals(Workflow.DEFAULT_START_TIME, wf1.getStartTime());
        
        Workflow wf2 = cfg.findWorkflow(new WorkflowID("workflow-2"));
        Assert.assertEquals("workflow-2", wf2.getID().toString());
        Assert.assertEquals(66, wf2.getMaxRetryCount());
        Assert.assertEquals(new ScheduledTime("2014-03-10T12:34:56.789Z"), wf2.getStartTime());
    }

    @Test
    public void propertiesForCronAreCorrectlySet() throws Exception {
        Workflow wf = parseDir("cron-task-test").findWorkflow(new WorkflowID("workflow-1"));
        Assert.assertEquals("workflow-1", wf.getID().toString());

        CronSchedule schedule = (CronSchedule) wf.getSchedule();
        Assert.assertEquals(schedule.getCronExpression(), "0 12 * * * ?");
    }

    @Test
    public void canFindWorkflow() throws Exception {
        WorkflowConfiguration cfg = parseDir("properties-test");
        Workflow wf1 = cfg.findWorkflow(new WorkflowID("workflow-1"));
        Assert.assertEquals(55, wf1.getMaxRetryCount());
        Assert.assertNull(cfg.findWorkflow(new WorkflowID("foobar")));
    }
    
    @Test
    public void idMustExist() throws Exception {
        expectMessage("id-missing", "Workflow ID must be a string");
    }

    @Test
    public void idMustBeAString() throws Exception {
        expectMessage("id-not-a-string", "Workflow ID must be a string");
    }
    
    @Test
    public void maxRetryCountUsesZeroIfNotSet() throws Exception {
        Workflow wf = parseDir("maxretrycount-missing").findWorkflow(new WorkflowID("workflow-1"));
        Assert.assertEquals(0, wf.getMaxRetryCount());
    }
    
    @Test
    public void maxRetryCountMustBeANumber() throws Exception {
        expectMessage("maxretrycount-not-a-number", "Cannot convert foo to java.lang.Integer");
    }
    
    @Test
    public void doesntFailOnSingleWorkflowError() throws Exception {
        WorkflowConfiguration cfg = parseDir("single-error");
        Assert.assertEquals(2, cfg.getWorkflows().size());
        Assert.assertNotNull(cfg.findWorkflow(new WorkflowID("workflow-1")));
        Assert.assertNotNull(cfg.findWorkflow(new WorkflowID("workflow-3")));
    }
    
    @Test
    public void doesntAllowDuplicateIDs() throws Exception {
        // Directory contains 2 workflows, but one will be dropped because of duplicate ID.
        WorkflowConfiguration cfg = parseFile("duplicate-ids");
        Assert.assertEquals(1, cfg.getWorkflows().size());
    }
    
    @Test
    public void scopesAreSeparate() throws Exception {
        parseNamedFile("separate-scopes", "workflow-1");
        parseNamedFile("separate-scopes", "workflow-2");
    }
    
    @Test
    public void defaultsWork() throws Exception {
        parseNamedFile("uses-defaults", "workflow-1");
    }
    
    public static WorkflowConfiguration parseFile(String label) throws Exception {
        return parseNamedFile(label, "workflow-1");
    }

    private static WorkflowConfiguration parseNamedFile(String label, String workflowName) throws URISyntaxException,
            Exception {
        File dir = getConfigurationDir(label);
        File defaults = getDefaultsDir();
        File workflow = new File(dir, workflowName + "." + WorkflowConfigurationParser.WORKFLOW_FILE_EXTENSION);
        WorkflowConfigurationParser workflowConfigurationParser = new WorkflowConfigurationParser(defaults);
        workflowConfigurationParser.parseFile(workflow);
        return workflowConfigurationParser.getWorkflowConfiguration();
    }
    
    public static WorkflowConfiguration parseDir(String label) throws Exception {
        File dir = getConfigurationDir(label);
        File defaults = getDefaultsDir();
        return new WorkflowConfigurationParser(defaults).parseConfiguration(dir).getWorkflowConfiguration();
    }

    public static File getConfigurationDir(String label) throws URISyntaxException {
        String path = "com/collective/celos/workflow-configuration-test/" + label;
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        return new File(resource.toURI());
    }
    
    public static File getDefaultsDir() throws URISyntaxException {
        String path = "com/collective/celos/defaults";
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        return new File(resource.toURI());
    }
    
    private void expectMessage(String label, String message) throws AssertionError {
        try {
            parseFile(label);
        } catch(Exception e) {
            if (e.getMessage().contains(message)) {
                return;
            }
        }
        throw new AssertionError();
    }
    

}
