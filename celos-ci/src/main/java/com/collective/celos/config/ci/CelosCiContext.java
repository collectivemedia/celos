package com.collective.celos.config.ci;


import com.collective.celos.deploy.JScpWorker;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.File;

public class CelosCiContext {

    public static enum Mode {
        DEPLOY, UNDEPLOY, TEST
    }

    private final CelosCiTarget target;
    private final String userName;
    private final Mode mode;
    private final File deployDir;
    private final String workflowName;
    private final FileSystem fileSystem;
    private final String hdfsPrefix;

    public CelosCiContext(CelosCiTarget target,
                          String userName,
                          Mode mode,
                          File deployDir,
                          String workflowName, String hdfsPrefix) throws Exception {
        this.target = target;
        this.userName = userName;
        this.mode = mode;
        this.deployDir = deployDir;
        this.workflowName = workflowName;
        this.hdfsPrefix = hdfsPrefix;
        this.fileSystem = getFileSystem(userName, target);
    }

    private FileSystem getFileSystem(String username, CelosCiTarget target) throws Exception {
        JScpWorker jscpWorker = new JScpWorker(username, target.getScpSecuritySettings());
        Configuration conf = new Configuration();

        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

        conf.addResource(jscpWorker.getRemoteFileIS(target.getPathToHdfsSite()));
        conf.addResource(jscpWorker.getRemoteFileIS(target.getPathToCoreSite()));

        UserGroupInformation.setConfiguration(conf);

        return FileSystem.get(conf);
    }


    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public String getUserName() {
        return userName;
    }

    public Mode getMode() {
        return mode;
    }

    public File getDeployDir() {
        return deployDir;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public CelosCiTarget getTarget() {
        return target;
    }

    public String getHdfsPrefix() {
        return hdfsPrefix;
    }
}
