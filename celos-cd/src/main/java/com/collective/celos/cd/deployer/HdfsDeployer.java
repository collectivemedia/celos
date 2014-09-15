package com.collective.celos.cd.deployer;

import com.collective.celos.cd.config.Config;
import org.apache.commons.vfs2.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.File;

public class HdfsDeployer {

    private static final String REMOTE_HDFS_PATTERN = "/user/%s/app/%s";
    private static final String LOCAL_HDFS_PATTERN = "%s/hdfs";

    private Config config;
    private JScpWorker jscpWorker;

    public HdfsDeployer(Config config) throws FileSystemException {
        this.jscpWorker = new JScpWorker(config);
        this.config = config;
    }

    public void undeploy() throws Exception {
        Configuration conf = getConfiguration();
        FileSystem fs = FileSystem.get(conf);

        Path dst = new Path(String.format(REMOTE_HDFS_PATTERN, config.getUserName(), config.getWorkflowName()));
        if (fs.exists(dst)) {
            fs.delete(dst, true);
        }
    }

    public void deploy() throws Exception {

        final String hdfsDirLocalPath = String.format(LOCAL_HDFS_PATTERN, config.getPathToWorkflow());

        final File hdfsDirLocal = new File(hdfsDirLocalPath);
        if (hdfsDirLocal.exists()) {

            Configuration conf = getConfiguration();

            final FileSystem fs = FileSystem.get(conf);

            Path dst = new Path(String.format(REMOTE_HDFS_PATTERN, config.getUserName(), config.getWorkflowName()));
            if (fs.exists(dst)) {
                fs.delete(dst, true);
            }
            fs.mkdirs(dst);

            String[] childFiles = hdfsDirLocal.list();
            for (String child : childFiles) {
                fs.copyFromLocalFile(new Path(hdfsDirLocalPath, child), dst);
            }
        }
    }

    private Configuration getConfiguration() throws Exception {
        Configuration conf = new Configuration();

        conf.addResource(jscpWorker.getRemoteFileIS(config.getPathToHdfsSite()));
        conf.addResource(jscpWorker.getRemoteFileIS(config.getPathToCoreSite()));

        UserGroupInformation.setConfiguration(conf);

        return conf;
    }


}
