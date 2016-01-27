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
package com.collective.celos.ui;

import java.net.MalformedURLException;
import java.net.URL;

import com.collective.celos.ui.UICommandLine;
import com.collective.celos.ui.UICommandLineParser;
import org.junit.Assert;
import org.junit.Test;

public class UICommandLineParserTest {

    @Test
    public void testParsesOk() throws Exception {
        UICommandLineParser parser = new UICommandLineParser();
        String[] clParams = ("--celos http://localhost:8888 --port 1234").split(" ");

        UICommandLine commandLine = parser.parse(clParams);
        Assert.assertEquals(commandLine.getCelosUrl(), new URL("http://localhost:8888"));
        Assert.assertEquals(commandLine.getHueUrl(), null);
        Assert.assertEquals(commandLine.getPort(), 1234);

    }
    
    @Test
    public void testParsesHueURL() throws Exception {
        UICommandLineParser parser = new UICommandLineParser();
        String[] clParams = ("--celos http://localhost:8888 --port 1234 --hue http://example.com").split(" ");

        UICommandLine commandLine = parser.parse(clParams);
        Assert.assertEquals(commandLine.getCelosUrl(), new URL("http://localhost:8888"));
        Assert.assertEquals(commandLine.getHueUrl(), new URL("http://example.com"));
        Assert.assertEquals(commandLine.getPort(), 1234);

    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPortMissing() throws Exception {
        UICommandLineParser parser = new UICommandLineParser();
        String[] clParams = ("--celos localhost:8888").split(" ");

        parser.parse(clParams);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCelosURLMissing() throws Exception {
        UICommandLineParser parser = new UICommandLineParser();
        String[] clParams = ("--port 8888").split(" ");

        parser.parse(clParams);
    }

    @Test(expected = MalformedURLException.class)
    public void testWrongAddress() throws Exception {
        UICommandLineParser parser = new UICommandLineParser();
        String[] clParams = ("--celos localhost:8888 --port 1234").split(" ");

        parser.parse(clParams);
    }

    @Test(expected = NumberFormatException.class)
    public void testWrongPort() throws Exception {
        UICommandLineParser parser = new UICommandLineParser();
        String[] clParams = ("--celos http://localhost:8888 --port abc").split(" ");

        parser.parse(clParams);
    }

}
