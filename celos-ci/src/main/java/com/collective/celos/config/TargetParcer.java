package com.collective.celos.config;

import com.collective.celos.cd.deployer.JScpWorker;
import org.apache.commons.vfs2.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by akonopko on 9/9/14.
 */
public class TargetParcer {

    public static final String TGT_CELOS_URI = "celos.uri";

    public static final String TGT_CELOS_WORKFLOW_DIR = "celos.workflow.dir";
    public static final String TGT_CELOS_DEF_DIR = "celos.defaults.dir";
    public static final String TGT_CELOS_DB_DIR = "celos.db.dir";
    public static final String TGT_HADOOP_HDFS_SITE_XML = "hadoop.hdfs-site.xml";
    public static final String TGT_HADOOP_CORE_SITE_XML = "hadoop.core-site.xml";

    private Config config;
    private JScpWorker worker;

    public TargetParcer(Config config) throws FileSystemException {
        this.worker = new JScpWorker(config);
        this.config = config;
    }

    public void process() throws Exception {
        InputStream is = worker.getRemoteFileIS(config.getTargetFile());
        HashMap<String, String> result = new ObjectMapper().readValue(is, HashMap.class);

        config.setCelosWorkflowsDirUri(result.get(TGT_CELOS_WORKFLOW_DIR));
        config.setCelosDbDirUri(result.get(TGT_CELOS_DB_DIR));
        config.setCelosDefaultsDirUri(result.get(TGT_CELOS_DEF_DIR));

        config.setPathToCoreSite(result.get(TGT_HADOOP_CORE_SITE_XML));
        config.setPathToHdfsSite(result.get(TGT_HADOOP_HDFS_SITE_XML));

    }

}
