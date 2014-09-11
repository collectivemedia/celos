package com.collective.celos.ci;

import com.collective.celos.cd.CelosCd;
import com.collective.celos.config.Config;
import com.collective.celos.config.WorkflowTestConfig;
import com.collective.celos.server.CelosServer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

/**
 * Created by akonopko on 9/9/14.
 */
public class CelosCi {

    private static HttpClient client = new DefaultHttpClient();

    public static void main(String... args) throws Exception {

        String pathToTestConfig = "/home/akonopko/work/celos2/samples/wordcount/testconfig.json";

        WorkflowTestConfig testConfig = new WorkflowTestConfig(new FileInputStream(pathToTestConfig));

        File celosWorkDir = new File(testConfig.getCelosPath());
        if (celosWorkDir.exists()) {
            celosWorkDir.delete();
        }

        final File workflowDir = new File(celosWorkDir, "workflows");
        workflowDir.mkdirs();
        final File defaultsDir = new File(celosWorkDir, "defaults");
        defaultsDir.mkdirs();
        final File dbDir = new File(celosWorkDir, "db");
        dbDir.mkdirs();

        CelosServer celosServer = new CelosServer();
        final Integer port = celosServer.startServer(Collections.<String, String>emptyMap(),
                workflowDir.toString(),
                defaultsDir.toString(),
                dbDir.toString());


        Config deployConfig = new Config();
        deployConfig.setCelosWorkflowsDirUri(workflowDir.toString());
        deployConfig.setCelosDefaultsDirUri(defaultsDir.toString());
        deployConfig.setCelosDbDirUri(dbDir.toString());

        deployConfig.setPathToCoreSite(testConfig.getHadoopCoreSiteXml());
        deployConfig.setPathToHdfsSite(testConfig.getHadoopHdfsSiteXml());
        deployConfig.setMode(Config.Mode.DEPLOY);
        deployConfig.setWorkflowName(testConfig.getWorkflowName());
        deployConfig.setPathToWorkflow(testConfig.getPathToWorkflow());

        System.out.println("Deploying workflow");
        CelosCd.runFromConfig(deployConfig);

        HttpPost post = new HttpPost("http://localhost:" + port + "/scheduler?time=" + testConfig.getSampleTime());
        HttpGet get = new HttpGet("http://localhost:" + port + "/workflow?time=" + testConfig.getSampleTime() + "&id=" + testConfig.getWorkflowName());
        do {
            System.out.println("Ping...");
            iterateScheduler(post);
            Thread.sleep(2000);
        } while (isWorkflowRunning(get));
        System.out.println("Job is finished");
        celosServer.stopServer();
    }

    private static void iterateScheduler(HttpPost post) throws IOException {
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
