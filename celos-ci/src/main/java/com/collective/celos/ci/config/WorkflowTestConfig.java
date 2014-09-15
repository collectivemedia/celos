package com.collective.celos.ci.config;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class WorkflowTestConfig {

    private final static String SAMPLE_TIME_START_PROP = "sample.time.start";
    private final static String SAMPLE_TIME_END_PROP = "sample.time.end";
    private final static String CELOS_PORT = "port";
    private final static String CELOS_PATH = "celos.path";
    private final static String TARGET_FILE = "target.file";
    private final static String DEFAULTS_FILE = "defaults.file";

    private final String sampleTimeStart;
    private final String sampleTimeEnd;
    private final String celosPort;
    private final String celosPath;
    private final String targetFile;
    private final String defaultsFile;

    public WorkflowTestConfig(InputStream configIS) throws IOException {

        HashMap<String, String> result = new ObjectMapper().readValue(configIS, HashMap.class);

        sampleTimeStart = result.get(SAMPLE_TIME_START_PROP);
        sampleTimeEnd = result.get(SAMPLE_TIME_END_PROP);
        celosPort = result.get(CELOS_PORT);
        celosPath = result.get(CELOS_PATH);
        targetFile = result.get(TARGET_FILE);
        defaultsFile = result.get(DEFAULTS_FILE);
    }

    public String getSampleTimeStart() {
        return sampleTimeStart;
    }

    public String getSampleTimeEnd() {
        return sampleTimeEnd;
    }

    public String getCelosPort() {
        return celosPort;
    }

    public String getCelosPath() {
        return celosPath;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public String getDefaultsFile() {
        return defaultsFile;
    }
}
