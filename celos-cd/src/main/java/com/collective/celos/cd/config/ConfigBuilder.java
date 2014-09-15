package com.collective.celos.cd.config;

import com.collective.celos.cd.deployer.JScpWorker;
import org.apache.commons.vfs2.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;

public class ConfigBuilder {

    public static final String CELOS_WORKFLOW_DIR = "celos.workflow.dir";
    public static final String HADOOP_HDFS_SITE_XML = "hadoop.hdfs-site.xml";
    public static final String HADOOP_CORE_SITE_XML = "hadoop.core-site.xml";
    public static final String PATH_TO_WF = "workflow.path";
    public static final String WF_NAME = "workflow.name";
    public static final String USER_NAME = "username";
    public static final String SEC_SETTINGS = "security.settings";

    private String targetFile;
    private Config.Mode mode;
    private JScpWorker worker;

    public ConfigBuilder(String userName, String securitySettings, String targetFile, Config.Mode mode) throws FileSystemException {
        this.worker = new JScpWorker(userName, securitySettings);
    }

    public Config build() throws Exception {
        InputStream is = worker.getRemoteFileIS(targetFile);
        HashMap<String, String> result = new ObjectMapper().readValue(is, HashMap.class);

        return new Config(result.get(SEC_SETTINGS),
                result.get(USER_NAME),
                mode,
                result.get(HADOOP_HDFS_SITE_XML),
                result.get(HADOOP_CORE_SITE_XML),
                result.get(PATH_TO_WF),
                result.get(WF_NAME),
                result.get(CELOS_WORKFLOW_DIR));
    }

}
