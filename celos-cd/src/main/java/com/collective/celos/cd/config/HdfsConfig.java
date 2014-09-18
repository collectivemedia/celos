package com.collective.celos.cd.config;

import com.collective.celos.cd.deployer.JScpWorker;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * Created by akonopko on 9/18/14.
 */
public class HdfsConfig {

    private FileSystem fileSystem;

    public HdfsConfig(String username, CelosCdTarget target) throws Exception {
        Configuration conf = getConfiguration(username, target);
        this.fileSystem = FileSystem.get(conf);
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    private Configuration getConfiguration(String username, CelosCdTarget target) throws Exception {
        JScpWorker jscpWorker = new JScpWorker(username, target.getScpSecuritySettings());
        Configuration conf = new Configuration();

        conf.addResource(jscpWorker.getRemoteFileIS(target.getPathToHdfsSite()));
        conf.addResource(jscpWorker.getRemoteFileIS(target.getPathToCoreSite()));

        UserGroupInformation.setConfiguration(conf);

        return conf;
    }

}
