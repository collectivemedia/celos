package com.collective.celos.ci.config.deploy;

import com.collective.celos.ci.deploy.JScpWorker;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;

public class CelosCiTargetParser {

    public static final String SEC_SETTINGS = "security.settings";
    public static final String CELOS_WORKFLOW_DIR = "celos.workflow.dir";
    public static final String HADOOP_HDFS_SITE_XML = "hadoop.hdfs-site.xml";
    public static final String HADOOP_CORE_SITE_XML = "hadoop.core-site.xml";
    public static final String DEFAULTS_FILE_URI = "defaults.file.uri";

    private JScpWorker worker;

    public CelosCiTargetParser(String userName) throws FileSystemException {
        this.worker = new JScpWorker(userName, JScpWorker.DEFAULT_SECURITY_SETTINGS);
    }

    public CelosCiTarget parse(String targetFileUri) throws Exception {
        FileObject file = worker.getFileObjectByUri(targetFileUri);
        InputStream is = file.getContent().getInputStream();
        HashMap<String, String> result = new ObjectMapper().readValue(is, HashMap.class);

        String secSetts = result.get(SEC_SETTINGS);
        if (secSetts == null) {
            secSetts = JScpWorker.DEFAULT_SECURITY_SETTINGS;
        }

        return new CelosCiTarget(secSetts,
                result.get(HADOOP_HDFS_SITE_XML),
                result.get(HADOOP_CORE_SITE_XML),
                result.get(CELOS_WORKFLOW_DIR),
                result.get(DEFAULTS_FILE_URI));
    }

}
