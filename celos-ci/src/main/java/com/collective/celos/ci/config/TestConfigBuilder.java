package com.collective.celos.ci.config;

import com.collective.celos.ScheduledTime;
import com.collective.celos.cd.deployer.JScpWorker;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class TestConfigBuilder {

    private final static String SAMPLE_TIME_START_PROP = "sample.time.start";
    private final static String SAMPLE_TIME_END_PROP = "sample.time.end";

    private final JScpWorker worker;

    public TestConfigBuilder(String userName, String securitySettings) throws IOException {
        this.worker = new JScpWorker(userName, securitySettings);
    }

    public TestConfig build(String testConfigUri) throws Exception {
        InputStream is = worker.getRemoteFileIS(testConfigUri);
        HashMap<String, String> result = new ObjectMapper().readValue(is, HashMap.class);

        return new TestConfig(
                new ScheduledTime(result.get(SAMPLE_TIME_START_PROP)),
                new ScheduledTime(result.get(SAMPLE_TIME_END_PROP)));
    }
}
