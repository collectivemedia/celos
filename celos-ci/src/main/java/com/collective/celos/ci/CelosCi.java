package com.collective.celos.ci;

import com.collective.celos.ScheduledTime;
import com.collective.celos.ScheduledTimeFormatter;
import com.collective.celos.cd.CelosCd;
import com.collective.celos.cd.config.CelosCdContext;
import com.collective.celos.cd.deployer.JScpWorker;
import com.collective.celos.ci.config.TestContext;
import com.collective.celos.ci.config.TestContextBuilder;
import com.collective.celos.server.CelosServer;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class CelosCi {

    public static final String WORKFLOW_DIR_CELOS_PATH = "workflows";
    public static final String DEFAULTS_DIR_CELOS_PATH = "defaults";
    public static final String DB_DIR_CELOS_PATH = "db";
    public static final String HDFS_PREFIX = "HDFS_PREFIX";

    private static HttpClient client = new DefaultHttpClient();
    private static ScheduledTimeFormatter timeFormatter = new ScheduledTimeFormatter();


    public static void main(String... args) throws Exception {

//        args = "--deployDir /home/akonopko/work/celos2/samples/wordcount2 --target sftp://107.170.177.172/home/akonopko/target.json --workflowName wordcount".split(" ");
//        args = "--deployDir /home/akonopko/work/celos2/samples/wordcount2 --target sftp://celos001/home/akonopko/target.json --workflowName wordcount".split(" ");

        TestContextBuilder contextBuilder = new TestContextBuilder();
        TestContext testContext = contextBuilder.parse(args);
        if (testContext == null) {
            contextBuilder.printHelp(80, 5, 3, true, System.out);
        } else {
            Path tempDir = Files.createTempDirectory("celos");
            File celosWorkDir = tempDir.toFile();
            FileUtils.forceDeleteOnExit(celosWorkDir);

            System.out.println("Celos created at: " + tempDir.toAbsolutePath().toString());
            System.out.println("HDFS prefix is: " + testContext.getHdfsPrefix());

            File workflowDir = new File(celosWorkDir, WORKFLOW_DIR_CELOS_PATH);
            File defaultsDir = new File(celosWorkDir, DEFAULTS_DIR_CELOS_PATH);
            File dbDir = new File(celosWorkDir, DB_DIR_CELOS_PATH);

            workflowDir.mkdirs();
            defaultsDir.mkdirs();
            dbDir.mkdirs();

            testContext.setCelosWorkflowDirUri(workflowDir);
            testContext.setCelosDefaultsDirUri(defaultsDir);
            testContext.setCelosDbDirUri(dbDir);

            JScpWorker worker = new JScpWorker(testContext.getUserName(), testContext.getTarget().getScpSecuritySettings());
            FileObject remoteDefaultsFile = worker.getFileObjectByUri(testContext.getTarget().getDefaultsFile());
            if (remoteDefaultsFile.exists()) {
                FileObject localDefaultsFile = worker.getFileObjectByUri(new File(defaultsDir, remoteDefaultsFile.getName().getBaseName()).toString());
                localDefaultsFile.copyFrom(remoteDefaultsFile, Selectors.SELECT_SELF);
            }

            final CelosServer celosServer = new CelosServer();

            try {

                Integer port = celosServer.startServer(ImmutableMap.of(HDFS_PREFIX, testContext.getHdfsPrefix()), workflowDir.toString(), defaultsDir.toString(), dbDir.toString());
                testContext.setCelosPort(port);

                CelosCdContext celosCdContext = createCelosCdContext(testContext);

                System.out.println("Deploying workflow " + testContext.getWorkflowName());
                CelosCd.runForContext(celosCdContext);

                runCelosScheduler(testContext);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("Job is finished");
                celosServer.stopServer();
            }

        }
    }

    private static CelosCdContext createCelosCdContext(TestContext context) {
        return new CelosCdContext(context.getTarget(),
                context.getUserName(),
                CelosCdContext.Mode.DEPLOY,
                context.getDeployDir(),
                context.getWorkflowName(),
                context.getCelosWorkflowDirUri().toString(),
                context.getHdfsPrefix());
    }

    private static void runCelosScheduler(TestContext testContext) throws IOException {
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

    private static boolean isThereAnyRunningWorkflows(Integer port, WorkflowsList workflowsList, ScheduledTime schedTime) throws IOException {
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

    private static void iterateScheduler(Integer port, ScheduledTime schedTime) throws IOException {
        String schedTimeStr = getTimeForHttpApi(schedTime);
        System.out.println("Touching Scheduler on " + schedTimeStr + "...");

        HttpPost post = new HttpPost("http://localhost:" + port + "/scheduler?time=" + schedTimeStr);
        HttpResponse postResponse = client.execute(post);
        EntityUtils.consume(postResponse.getEntity());
    }

    private static String getTimeForHttpApi(ScheduledTime schedTime) {
        //get for 1 second more cause we do not include slot of the time provided
        return timeFormatter.formatPretty(schedTime.plusSeconds(1));
    }

    private static boolean isWorkflowRunning(HttpGet get) throws IOException {
        HttpResponse getResponse = client.execute(get);
        StringWriter writer = new StringWriter();
        IOUtils.copy(getResponse.getEntity().getContent(), writer);
        String resultStr = writer.toString();

        return resultStr.contains("READY") || resultStr.contains("RUNNING");
    }

}
