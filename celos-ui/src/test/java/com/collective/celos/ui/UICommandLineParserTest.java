package com.collective.celos.ui;

import java.net.MalformedURLException;
import java.net.URL;

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
