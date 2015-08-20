package com.collective.celos;

import junit.framework.Assert;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class JettyServerTest {

    @Test
    public void testServerStartsSpecifyPort() throws Exception {
        int port = Util.getFreePort();

        JettyServer jettyServer = new JettyServer();
        jettyServer.start(port);

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet workflowListGet = new HttpGet("http://localhost:" + port + "/version");
        HttpResponse response = httpClient.execute(workflowListGet);
        EntityUtils.consume(response.getEntity());

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
    }


    @Test(expected = HttpHostConnectException.class)
    public void testServerStopsSpecifyPort() throws Exception {
        int port = Util.getFreePort();

        JettyServer jettyServer = new JettyServer();
        jettyServer.start(port);

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet workflowListGet = new HttpGet("http://localhost:" + port + "/version");
        HttpResponse response = httpClient.execute(workflowListGet);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        EntityUtils.consume(response.getEntity());
        jettyServer.stop();

        httpClient.execute(workflowListGet);
    }

}
