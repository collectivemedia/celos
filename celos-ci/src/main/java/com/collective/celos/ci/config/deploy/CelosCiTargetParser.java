package com.collective.celos.ci.config.deploy;

import com.collective.celos.ci.deploy.JScpWorker;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

public class CelosCiTargetParser {

    public static final String WORKFLOWS_DIR_URI = "workflows.dir.uri";
    public static final String HADOOP_HDFS_SITE_XML = "hadoop.hdfs-site.xml";
    public static final String HADOOP_CORE_SITE_XML = "hadoop.core-site.xml";
    public static final String DEFAULTS_FILE_URI = "defaults.dir.uri";
    public static final String HIVE_JDBC_URL = "hive.jdbc.url";

    private JScpWorker worker;

    public CelosCiTargetParser(String userName) throws FileSystemException {
        this.worker = new JScpWorker(userName);
    }

    public CelosCiTarget parse(URI targetFileUri) throws Exception {
        FileObject file = worker.getFileObjectByUri(targetFileUri);
        InputStream is = file.getContent().getInputStream();
        HashMap<String, String> result = new ObjectMapper().readValue(is, HashMap.class);
        URI workflowDir = result.get(WORKFLOWS_DIR_URI) != null ? URI.create(result.get(WORKFLOWS_DIR_URI)) : null;
        URI defaultsFile = result.get(DEFAULTS_FILE_URI) != null ? URI.create(result.get(DEFAULTS_FILE_URI)) : null;
        URI hiveJdbc = result.get(HIVE_JDBC_URL) != null ? URI.create(result.get(HIVE_JDBC_URL)) : null;
        return new CelosCiTarget(URI.create(result.get(HADOOP_HDFS_SITE_XML)),
                URI.create(result.get(HADOOP_CORE_SITE_XML)),
                workflowDir, defaultsFile, hiveJdbc);
    }

}
