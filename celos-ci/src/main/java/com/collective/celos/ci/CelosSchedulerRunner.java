package com.collective.celos.ci;

import com.collective.celos.ScheduledTime;
import com.collective.celos.ScheduledTimeFormatter;
import com.collective.celos.ci.config.TestContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by akonopko on 9/18/14.
 */
public class CelosSchedulerRunner {

    private HttpClient client;
    private ScheduledTimeFormatter timeFormatter;
    private TestContext testContext;

    public CelosSchedulerRunner(TestContext testContext) {
        this.client = new DefaultHttpClient();
        this.timeFormatter = new ScheduledTimeFormatter();
        this.testContext = testContext;
    }

    public void runCelosScheduler() throws IOException {
        WorkflowsList workflowsList = getWorkflowList(testContext.getCelosPort());

        ScheduledTime startTime = testContext.getTestConfig().getSampleTimeStart();
        ScheduledTime actualTime = startTime;
        ScheduledTime endTime = testContext.getTestConfig().getSampleTimeEnd();

        while (!actualTime.getDateTime().isAfter(endTime.getDateTime())) {
            iterateScheduler(testContext.getCelosPort(), actualTime);
            if (!isThereAnyRunningWorkflows(testContext.getCelosPort(), workflowsList, actualTime)) {
                actualTime = new ScheduledTime(actualTime.getDateTime().plusHours(1));
            }
        }
    }

    private WorkflowsList getWorkflowList(Integer port) throws IOException {
        HttpGet workflowListGet = new HttpGet("http://localhost:" + port + "/workflow-list");
        HttpResponse getResponse = client.execute(workflowListGet);
        return new ObjectMapper().readValue(getResponse.getEntity().getContent(), WorkflowsList.class);
    }

    public static class WorkflowsList {
        private String[] ids;

        public String[] getIds() {
            return ids;
        }

    }

    private boolean isThereAnyRunningWorkflows(Integer port, WorkflowsList workflowsList, ScheduledTime schedTime) throws IOException {
        String schedTimeStr = getTimeForHttpApi(schedTime);
        for (String workflowID : workflowsList.getIds()) {
            HttpGet get = new HttpGet("http://localhost:" + port + "/workflow?id=" + workflowID + "&time=" + schedTimeStr);
            if (isWorkflowRunning(get)) {
                System.out.println("There is workflow running: " + workflowID + " at " + schedTimeStr);
                return true;
            }
        }
        return false;
    }

    private void iterateScheduler(Integer port, ScheduledTime schedTime) throws IOException {
        String schedTimeStr = getTimeForHttpApi(schedTime);
        System.out.println("Touching Scheduler on " + schedTimeStr + "...");

        HttpPost post = new HttpPost("http://localhost:" + port + "/scheduler?time=" + schedTimeStr);
        HttpResponse postResponse = client.execute(post);
        EntityUtils.consume(postResponse.getEntity());
    }

    private String getTimeForHttpApi(ScheduledTime schedTime) {
        //get for 1 second more cause we do not include slot of the time provided
        return timeFormatter.formatPretty(schedTime.plusSeconds(1));
    }

    private boolean isWorkflowRunning(HttpGet get) throws IOException {
        HttpResponse getResponse = client.execute(get);
        StringWriter writer = new StringWriter();
        IOUtils.copy(getResponse.getEntity().getContent(), writer);
        String resultStr = writer.toString();

        return resultStr.contains("READY") || resultStr.contains("RUNNING");
    }


}
