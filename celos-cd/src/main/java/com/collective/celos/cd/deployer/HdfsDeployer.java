package com.collective.celos.cd.deployer;

import com.collective.celos.cd.config.CelosCdContext;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class HdfsDeployer {

    private static final String REMOTE_HDFS_PATTERN = "%s/user/%s/app/%s";
    private static final String LOCAL_HDFS_PATTERN = "%s/hdfs";
    private static final String LOCAL_INPUT_PATTERN = "%s/input";

    private static enum FixtureType { PLAIN };

    private CelosCdContext config;
    private JScpWorker jscpWorker;
    private Map<FixtureType, ? extends FixtureDeployWorker> fixtureDeployers;

    public HdfsDeployer(CelosCdContext config) throws FileSystemException {
        this.jscpWorker = new JScpWorker(config.getUserName(), config.getTarget().getScpSecuritySettings());
        this.config = config;
        this.fixtureDeployers = ImmutableMap.of(
                FixtureType.PLAIN, new PlainFixtureDeployWorker(config)
        );
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

        Configuration conf = getConfiguration();
        FileSystem fs = FileSystem.get(conf);

        placeHdfsFolder(fs);
        placeInputsFolder(fs);
    }

    private void placeInputsFolder(FileSystem fs) throws Exception {
        final String inputDirLocalPath = String.format(LOCAL_INPUT_PATTERN, config.getPathToWorkflow());
        final File inputDirLocal = new File(inputDirLocalPath);
        if (StringUtils.isNotEmpty(config.getHdfsPrefix()) && inputDirLocal.exists()) {
            for (File typeFile : inputDirLocal.listFiles()) {
                FixtureDeployWorker deployer = fixtureDeployers.get(FixtureType.valueOf(typeFile.getName().toUpperCase()));
                if (deployer == null) {
                    throw new RuntimeException("Cant find fixture deployer for " + typeFile.getName());
                }
                deployer.deploy(typeFile, fs);
            }
        }
    }

    private void placeHdfsFolder(FileSystem fs) throws IOException {
        final String hdfsDirLocalPath = String.format(LOCAL_HDFS_PATTERN, config.getPathToWorkflow());

        final File hdfsDirLocal = new File(hdfsDirLocalPath);
        if (hdfsDirLocal.exists()) {

            Path dst = new Path(String.format(REMOTE_HDFS_PATTERN, config.getHdfsPrefix(), config.getUserName(), config.getWorkflowName()));
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

        conf.addResource(jscpWorker.getRemoteFileIS(config.getTarget().getPathToHdfsSite()));
        conf.addResource(jscpWorker.getRemoteFileIS(config.getTarget().getPathToCoreSite()));

        UserGroupInformation.setConfiguration(conf);

        return conf;
    }


}
