package com.collective.celos.ci.config.testing;

import com.collective.celos.ScheduledTime;
import com.collective.celos.ci.config.testing.TestConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class TestConfigParser {

    private final static String SAMPLE_TIME_START_PROP = "sample.time.start";
    private final static String SAMPLE_TIME_END_PROP = "sample.time.end";

    private final static String TEST_CONFIG_PATH_PATTERN = "%s/test-config.json";

    public TestConfig parse(File testCasePath) throws Exception {
        File configPath = new File(String.format(TEST_CONFIG_PATH_PATTERN, testCasePath.getAbsolutePath()));
        InputStream is = new FileInputStream(configPath);
        HashMap<String, String> result = new ObjectMapper().readValue(is, HashMap.class);

        return new TestConfig(
                new ScheduledTime(result.get(SAMPLE_TIME_START_PROP)),
                new ScheduledTime(result.get(SAMPLE_TIME_END_PROP)));
    }
}
