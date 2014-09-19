package com.collective.celos.cd.config;

import com.collective.celos.cd.deployer.JScpWorker;
import org.apache.commons.vfs2.FileSystemException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;

public class TargetParcer {

    public static final String SEC_SETTINGS = "security.settings";
    public static final String CELOS_WORKFLOW_DIR = "celos.workflow.dir";
    public static final String HADOOP_HDFS_SITE_XML = "hadoop.hdfs-site.xml";
    public static final String HADOOP_CORE_SITE_XML = "hadoop.core-site.xml";
    public static final String DEFAULTS_FILE_URI = "defaults.file.uri";

    private JScpWorker worker;

    public TargetParcer(String userName, String securitySettings) throws FileSystemException {
        this.worker = new JScpWorker(userName, securitySettings);
    }

    public CelosCdTarget parse(String targetFileUri) throws Exception {
        InputStream is = worker.getRemoteFileIS(targetFileUri);
        HashMap<String, String> result = new ObjectMapper().readValue(is, HashMap.class);

        String secSetts = result.get(SEC_SETTINGS);
        if (secSetts == null) {
            secSetts = JScpWorker.DEFAULT_SECURITY_SETTINGS;
        }

        return new CelosCdTarget(secSetts,
                result.get(HADOOP_HDFS_SITE_XML),
                result.get(HADOOP_CORE_SITE_XML),
                result.get(CELOS_WORKFLOW_DIR),
                result.get(DEFAULTS_FILE_URI));
    }

}
