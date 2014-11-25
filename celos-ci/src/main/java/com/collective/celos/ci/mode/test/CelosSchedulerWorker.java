package com.collective.celos.ci.mode.test;

import com.collective.celos.ScheduledTime;
import com.collective.celos.ScheduledTimeFormatter;
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
public class CelosSchedulerWorker {

    private final HttpClient client;
    public static final int MIN_PAUSE = 5000;
    private final ScheduledTimeFormatter timeFormatter;
    private final int port;

    public CelosSchedulerWorker(int port) {
        this.port = port;
        this.client = new DefaultHttpClient();
        this.timeFormatter = new ScheduledTimeFormatter();
    }

    public void runCelosScheduler(TestCase testConfig) throws Exception {
        WorkflowsList workflowsList = getWorkflowList();

        ScheduledTime startTime = testConfig.getSampleTimeStart();
        ScheduledTime actualTime = startTime;
        ScheduledTime endTime = testConfig.getSampleTimeEnd();

        //one more iteration so celos goes to the ready and running state
        while (!actualTime.getDateTime().isAfter(endTime.getDateTime())) {
            iterateScheduler(port, actualTime);
            long timeMills = System.currentTimeMillis();
            if (!isThereAnyRunningWorkflows(port, workflowsList, actualTime)) {
                actualTime = new ScheduledTime(actualTime.getDateTime().plusHours(1));
            } else {
                makePause(timeMills);
            }
        }
    }

    private void makePause(long timeMills) throws InterruptedException {
        long sleepTime = MIN_PAUSE - (System.currentTimeMillis() - timeMills);
        if (sleepTime > 0) {
            Thread.sleep(sleepTime);
        }
    }

    private WorkflowsList getWorkflowList() throws IOException {
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
