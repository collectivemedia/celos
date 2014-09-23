package com.collective.celos.config.test;

import com.collective.celos.ScheduledTime;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class TestConfigBuilder {

    private final static String SAMPLE_TIME_START_PROP = "sample.time.start";
    private final static String SAMPLE_TIME_END_PROP = "sample.time.end";

    private final static String TEST_CONFIG_PATH_PATTERN = "%s/test-config.json";

    public TestConfig build(File workflowPath) throws Exception {
        File configPath = new File(String.format(TEST_CONFIG_PATH_PATTERN, workflowPath.getAbsolutePath()));
        InputStream is = new FileInputStream(configPath);
        HashMap<String, String> result = new ObjectMapper().readValue(is, HashMap.class);

        return new TestConfig(
                new ScheduledTime(result.get(SAMPLE_TIME_START_PROP)),
                new ScheduledTime(result.get(SAMPLE_TIME_END_PROP)));
    }
}
