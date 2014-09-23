package com.collective.celos.config;

import com.collective.celos.cd.deployer.JScpWorker;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * Created by akonopko on 9/18/14.
 */
public class HdfsConfig {

    private FileSystem fileSystem;

    public HdfsConfig(String username, CelosCiTarget target) throws Exception {
        Configuration conf = getConfiguration(username, target);
        this.fileSystem = FileSystem.get(conf);
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    private Configuration getConfiguration(String username, CelosCiTarget target) throws Exception {
        JScpWorker jscpWorker = new JScpWorker(username, target.getScpSecuritySettings());
        Configuration conf = new Configuration();

        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

        conf.addResource(jscpWorker.getRemoteFileIS(target.getPathToHdfsSite()));
        conf.addResource(jscpWorker.getRemoteFileIS(target.getPathToCoreSite()));

        UserGroupInformation.setConfiguration(conf);

        return conf;
    }

}
