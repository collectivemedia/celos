package com.collective.celos.ui;

import junit.framework.Assert;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class UICommandLineParserTest {

    @Test
    public void testParsesOk() throws Exception {
        UICommandLineParser parser = new UICommandLineParser();
        String[] clParams = ("--celosAddr http://localhost:8888 --port 1234").split(" ");

        UICommandLine commandLine = parser.parse(clParams);
        Assert.assertEquals(commandLine.getCelosUrl(), new URL("http://localhost:8888"));
        Assert.assertEquals(commandLine.getPort(), 1234);

    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPortMissing() throws Exception {
        UICommandLineParser parser = new UICommandLineParser();
        String[] clParams = ("--celosAddr localhost:8888").split(" ");

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
        String[] clParams = ("--celosAddr localhost:8888 --port 1234").split(" ");

        parser.parse(clParams);
    }

    @Test(expected = NumberFormatException.class)
    public void testWrongPort() throws Exception {
        UICommandLineParser parser = new UICommandLineParser();
        String[] clParams = ("--celosAddr http://localhost:8888 --port abc").split(" ");

        parser.parse(clParams);
    }

}
