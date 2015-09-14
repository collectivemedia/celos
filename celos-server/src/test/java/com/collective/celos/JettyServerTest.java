/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
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

        JettyServer jettyServer = new JettyServer();
        int port = jettyServer.start();

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet workflowListGet = new HttpGet("http://localhost:" + port  + "/workflow-list");
        HttpResponse response = httpClient.execute(workflowListGet);
        EntityUtils.consume(response.getEntity());

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
    }


    @Test(expected = HttpHostConnectException.class)
    public void testServerStopsSpecifyPort() throws Exception {

        JettyServer jettyServer = new JettyServer();
        int port = jettyServer.start();

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet workflowListGet = new HttpGet("http://localhost:" + port + "/workflow-list");
        HttpResponse response = httpClient.execute(workflowListGet);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        EntityUtils.consume(response.getEntity());
        jettyServer.stop();

        httpClient.execute(workflowListGet);
    }

}
