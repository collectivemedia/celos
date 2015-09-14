package com.collective.celos;

import junit.framework.Assert;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by akonopko on 22.12.14.
 */
public class JettyServerTest {

    @Test
    public void testServerStarts() throws Exception {
        JettyServer jettyServer = new JettyServer();
        int port = jettyServer.start();

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet workflowListGet = new HttpGet("http://localhost:" + port);
        HttpResponse response = httpClient.execute(workflowListGet);
        EntityUtils.consume(response.getEntity());

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 403);
    }


    @Test(expected = HttpHostConnectException.class)
    public void testServerStops() throws Exception {
        JettyServer jettyServer = new JettyServer();
        int port = jettyServer.start();

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet workflowListGet = new HttpGet("http://localhost:" + port);
        HttpResponse response = httpClient.execute(workflowListGet);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 403);
        EntityUtils.consume(response.getEntity());
        jettyServer.stop();

        httpClient.execute(workflowListGet);
    }

    @Test
    public void testServerStartsSpecifyPort() throws Exception {
        int port = getFreePort();

        JettyServer jettyServer = new JettyServer();
        jettyServer.start(port);

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet workflowListGet = new HttpGet("http://localhost:" + port);
        HttpResponse response = httpClient.execute(workflowListGet);
        EntityUtils.consume(response.getEntity());

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 403);
    }


    @Test(expected = HttpHostConnectException.class)
    public void testServerStopsSpecifyPort() throws Exception {
        int port = getFreePort();

        JettyServer jettyServer = new JettyServer();
        jettyServer.start(port);

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet workflowListGet = new HttpGet("http://localhost:" + port);
        HttpResponse response = httpClient.execute(workflowListGet);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 403);
        EntityUtils.consume(response.getEntity());
        jettyServer.stop();

        httpClient.execute(workflowListGet);
    }

    private int getFreePort() throws IOException {
        ServerSocket s = new ServerSocket(0);
        int port = s.getLocalPort();
        s.close();
        return port;
    }

}
