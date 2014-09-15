package com.collective.celos.ci;

import com.collective.celos.ScheduledTime;
import com.collective.celos.ScheduledTimeFormatter;
import com.collective.celos.cd.CelosCd;
import com.collective.celos.cd.config.Config;
import com.collective.celos.cd.config.ConfigBuilder;
import com.collective.celos.cd.deployer.JScpWorker;
import com.collective.celos.ci.config.WorkflowTestConfig;
import com.collective.celos.server.CelosServer;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FileUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

public class CelosCi {

    public static final String WORKFLOW_DIR_CELOS_PATH = "workflows";
    public static final String DEFAULTS_DIR_CELOS_PATH = "defaults";
    public static final String DB_DIR_CELOS_PATH = "db";

    private static HttpClient client = new DefaultHttpClient();
    private static ScheduledTimeFormatter timeFormatter = new ScheduledTimeFormatter();


    public static void main(String... args) throws Exception {

        String pathToTestConfig = "/home/akonopko/work/celos2/samples/wordcount/testconfig.json";

        WorkflowTestConfig testConfig = new WorkflowTestConfig(new FileInputStream(pathToTestConfig));

        File celosWorkDir = new File(testConfig.getCelosPath());
        FileUtil.fullyDelete(celosWorkDir);

        File workflowDir = new File(celosWorkDir, WORKFLOW_DIR_CELOS_PATH);
        File defaultsDir = new File(celosWorkDir, DEFAULTS_DIR_CELOS_PATH);
        File dbDir = new File(celosWorkDir, DB_DIR_CELOS_PATH);

        ConfigBuilder configBuilder = new ConfigBuilder(
                System.getProperty("user.name"),
                Config.DEFAULT_SECURITY_SETTINGS,
                testConfig.getTargetFile(),
                Config.Mode.DEPLOY);

        Config celosCdConfig = configBuilder.build();

        JScpWorker worker = new JScpWorker(celosCdConfig.getUserName(), celosCdConfig.getScpSecuritySettings());
//        worker.copyFileToRemote();

        CelosServer celosServer = new CelosServer();

        try {
            Integer port = celosServer.startServer(Collections.<String, String>emptyMap(), workflowDir.toString(), defaultsDir.toString(), dbDir.toString());

            System.out.println("Deploying workflow " + celosCdConfig.getWorkflowName());
            CelosCd.runFromConfig(celosCdConfig);

            runCelosScheduler(testConfig, port);

        } finally {
            System.out.println("Job is finished");
            celosServer.stopServer();
        }

    }

    private static void runCelosScheduler(WorkflowTestConfig testConfig, Integer port) throws IOException {
        WorkflowsList workflowsList = getWorkflowList(port);

        ScheduledTime startTime = new ScheduledTime(testConfig.getSampleTimeStart());
        ScheduledTime actualTime = startTime;
        ScheduledTime endTime = new ScheduledTime(testConfig.getSampleTimeEnd());

        while (!actualTime.getDateTime().isAfter(endTime.getDateTime())) {
            iterateScheduler(port, actualTime);
            if (!isThereAnyRunningWorkflows(port, workflowsList)) {
                actualTime = new ScheduledTime(actualTime.getDateTime().plusHours(1));
            }
        }
    }

    private static WorkflowsList getWorkflowList(Integer port) throws IOException {
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

    private static boolean isThereAnyRunningWorkflows(Integer port, WorkflowsList workflowsList) throws IOException {
        for (String workflowID : workflowsList.getIds()) {
            HttpGet get = new HttpGet("http://localhost:" + port + "/workflow?&id=" + workflowID);
            if (isWorkflowRunning(get)) {
                System.out.println("There is workflow running: " + workflowID);
                return true;
            }
        }
        return false;
    }

    private static void iterateScheduler(Integer port, ScheduledTime schedTime) throws IOException {
        String schedTimeStr = timeFormatter.formatPretty(schedTime);
        System.out.println("Touching Scheduler on " + schedTimeStr + "...");

        HttpPost post = new HttpPost("http://localhost:" + port + "/scheduler?time=" + schedTimeStr);
        HttpResponse postResponse = client.execute(post);
        EntityUtils.consume(postResponse.getEntity());
    }

    private static boolean isWorkflowRunning(HttpGet get) throws IOException {
        HttpResponse getResponse = client.execute(get);
        StringWriter writer = new StringWriter();
        IOUtils.copy(getResponse.getEntity().getContent(), writer);
        String resultStr = writer.toString();
        return resultStr.contains("READY") || resultStr.contains("RUNNING");
    }

}
