package com.collective.celos.ci.config.deploy;

import com.collective.celos.ci.deploy.JScpWorker;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

public class CelosCiTargetParser {

    /**
     * Mandatory target properties
     */
    public static final String WORKFLOWS_DIR_URI = "workflows.dir.uri";
    public static final String HADOOP_HDFS_SITE_XML = "hadoop.hdfs-site.xml";
    public static final String HADOOP_CORE_SITE_XML = "hadoop.core-site.xml";
    public static final String DEFAULTS_DIR_URI = "defaults.dir.uri";
    /**
     * Optional target properties
     */
    public static final String HIVE_JDBC_URL = "hive.jdbc.url";

    private JScpWorker worker;

    public CelosCiTargetParser(String userName) throws FileSystemException {
        this.worker = new JScpWorker(userName);
    }

    private String getNotNull(HashMap<String, String> map, String key) {
        String result = map.get(key);
        if (result == null) {
            throw new IllegalStateException(key + " was not specified in the target file");
        }
        return result;
    }

    public CelosCiTarget parse(URI targetFileUri) throws Exception {
        FileObject file = worker.getFileObjectByUri(targetFileUri);
        InputStream is = file.getContent().getInputStream();
        HashMap<String, String> result = new ObjectMapper().readValue(is, HashMap.class);

        URI hdfsSiteXml = URI.create(getNotNull(result, HADOOP_HDFS_SITE_XML));
        URI hdfsCoreXml = URI.create(getNotNull(result, HADOOP_CORE_SITE_XML));
        URI workflowDir = URI.create(getNotNull(result, WORKFLOWS_DIR_URI));
        URI defaultsFile = URI.create(getNotNull(result, DEFAULTS_DIR_URI));

        URI hiveJdbc = result.get(HIVE_JDBC_URL) != null ? URI.create(result.get(HIVE_JDBC_URL)) : null;

        return new CelosCiTarget(hdfsSiteXml, hdfsCoreXml, workflowDir, defaultsFile, hiveJdbc);
    }

}
