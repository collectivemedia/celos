package com.collective.celos.ci.config.deploy;

import com.collective.celos.Util;
import com.collective.celos.ci.deploy.JScpWorker;
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
    private final String hdfsRoot;

    public CelosCiContext(CelosCiTarget target,
                          String userName,
                          Mode mode,
                          File deployDir,
                          String workflowName,
                          String hdfsPrefix, String hdfsRoot) throws Exception {
        this.hdfsRoot = hdfsRoot;
        this.target = Util.requireNonNull(target);
        this.userName = Util.requireNonNull(userName);
        this.mode = Util.requireNonNull(mode);
        this.deployDir = deployDir;
        this.workflowName = Util.requireNonNull(workflowName);
        this.hdfsPrefix = Util.requireNonNull(hdfsPrefix);
        this.fileSystem = FileSystem.get(this.setupConfiguration(userName, target));
    }

    private Configuration setupConfiguration(String username, CelosCiTarget target) throws Exception {
        JScpWorker jscpWorker = new JScpWorker(username);
        Configuration conf = new Configuration();

        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

        conf.addResource(jscpWorker.getFileObjectByUri(target.getPathToHdfsSite()).getContent().getInputStream());
        conf.addResource(jscpWorker.getFileObjectByUri(target.getPathToCoreSite()).getContent().getInputStream());

        UserGroupInformation.setConfiguration(conf);

        return conf;
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

    public String getHdfsRoot() {
        return hdfsRoot;
    }
}
