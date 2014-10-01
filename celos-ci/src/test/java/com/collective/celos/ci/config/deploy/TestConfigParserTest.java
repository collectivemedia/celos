package com.collective.celos.ci.config.deploy;

import com.collective.celos.ScheduledTime;
import com.collective.celos.ci.config.testing.TestConfig;
import com.collective.celos.ci.config.testing.TestConfigParser;
import com.collective.celos.ci.deploy.JScpWorker;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;

/**
 * Created by akonopko on 9/29/14.
 */
public class TestConfigParserTest {

    @Test
    public void testParseTargetFile() throws Exception {
        TestConfigParser parser = new TestConfigParser();
        File targetFile = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config").toURI());

        TestConfig config = parser.parse(targetFile);

        Assert.assertEquals(config.getSampleTimeStart(), new ScheduledTime("2013-12-20T16:00Z"));
        Assert.assertEquals(config.getSampleTimeEnd(), new ScheduledTime("2013-12-20T18:00Z"));
    }
}
