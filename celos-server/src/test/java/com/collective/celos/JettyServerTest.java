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
    public void testServerStartsSpecifyPort() throws Exception {
        int port = getFreePort();

        JettyServer jettyServer = new JettyServer();
        jettyServer.start(port);

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet workflowListGet = new HttpGet("http://localhost:" + port);
        HttpResponse response = httpClient.execute(workflowListGet);
        EntityUtils.consume(response.getEntity());

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
    }


    @Test(expected = HttpHostConnectException.class)
    public void testServerStopsSpecifyPort() throws Exception {
        int port = getFreePort();

        JettyServer jettyServer = new JettyServer();
        jettyServer.start(port);

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet workflowListGet = new HttpGet("http://localhost:" + port);
        HttpResponse response = httpClient.execute(workflowListGet);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        EntityUtils.consume(response.getEntity());
        jettyServer.stop();

        httpClient.execute(workflowListGet);
    }

    /**
     * We dont use this method in production code now, cause it seems that sometimes port is left closed afterwards and Celos fails to setup Jetty instance with it
     */
    private int getFreePort() throws IOException {
        ServerSocket s = new ServerSocket(0);
        int port = s.getLocalPort();
        s.close();
        return port;
    }

}
