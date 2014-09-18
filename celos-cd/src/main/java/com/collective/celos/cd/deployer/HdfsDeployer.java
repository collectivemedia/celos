package com.collective.celos.cd.deployer;

import com.collective.celos.cd.config.CelosCdContext;
import com.collective.celos.cd.fixtures.FixturesHdfsWorkerManager;
import com.collective.celos.cd.fixtures.PlainFixtureDeployWorker;
import com.google.common.collect.ImmutableMap;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;

public class HdfsDeployer {

    private static final String REMOTE_HDFS_PATTERN = "%s/user/%s/app/%s";
    private static final String LOCAL_HDFS_PATTERN = "%s/hdfs";
    private static final String LOCAL_INPUT_PATTERN = "%s/input";

    private CelosCdContext context;
    private FixturesHdfsWorkerManager fixturesHdfsHelper;

    public HdfsDeployer(CelosCdContext context) throws Exception {
        this.context = context;
        this.fixturesHdfsHelper = new FixturesHdfsWorkerManager(context, ImmutableMap.of("PLAIN", new PlainFixtureDeployWorker(context)));
    }

    public void undeploy() throws Exception {
        Path dst = new Path(String.format(REMOTE_HDFS_PATTERN, context.getUserName(), context.getWorkflowName()));
        if (context.getFileSystem().exists(dst)) {
            context.getFileSystem().delete(dst, true);
        }
    }

    public void deploy() throws Exception {

        placeHdfsFolder();
        fixturesHdfsHelper.processLocalDir(String.format(LOCAL_INPUT_PATTERN, context.getDeployDir()));
    }

    private void placeHdfsFolder() throws IOException {
        FileSystem fs = context.getFileSystem();
        final String hdfsDirLocalPath = String.format(LOCAL_HDFS_PATTERN, context.getDeployDir());

        final File hdfsDirLocal = new File(hdfsDirLocalPath);
        if (hdfsDirLocal.exists()) {

            Path dst = new Path(String.format(REMOTE_HDFS_PATTERN, context.getHdfsPrefix(), context.getUserName(), context.getWorkflowName()));
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

}
