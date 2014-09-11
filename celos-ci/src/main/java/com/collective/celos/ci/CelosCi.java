package com.collective.celos.ci;

import com.collective.celos.config.Config;
import com.collective.celos.server.CelosServer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.Collections;

/**
 * Created by akonopko on 9/9/14.
 */
public class CelosCi {

    public static void main(String... args) throws Exception {

        final File celosWorkDir = new File("/home/akonopko/work/wcelos");
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
        celosServer.startServer(Collections.<String, String>emptyMap(),
                workflowDir.toString(),
                defaultsDir.toString(),
                dbDir.toString());


        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://localhost/scheduler");
                    while (true) {
                        HttpResponse response = client.execute(post);
                        EntityUtils.consume(response.getEntity());
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {

                }
            }
        });
        t2.start();


        Config config = new Config();
        config.setCelosWorkflowsDirUri(celosWorkDir.toString());
        config.setPathToCoreSite("/home/akonopko/core-site.xml");
        config.setPathToHdfsSite("/home/akonopko/hdfs-site.xml");
        config.setMode(Config.Mode.DEPLOY);
        config.setWorkflowName("celoscd");
        config.setPathToWorkflow("/home/akonopko/work/celos-ci/wf");

//        CelosCd.configParsed();


    }

}
