package com.collective.celos.ci.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;

public class HdfsDeployer {

    private static final String REMOTE_HDFS_PATTERN = "%s/user/celos/app/%s";
    private static final String LOCAL_HDFS_PATTERN = "%s/hdfs";

    private CelosCiContext context;

    public HdfsDeployer(CelosCiContext context) throws Exception {
        this.context = context;
    }

    public void undeploy() throws Exception {
        Path dst = new Path(String.format(REMOTE_HDFS_PATTERN, context.getUserName(), context.getWorkflowName()));
        if (context.getFileSystem().exists(dst)) {
            context.getFileSystem().delete(dst, true);
        }
    }

    public void deploy() throws Exception {

        FileSystem fs = context.getFileSystem();
        final String hdfsDirLocalPath = String.format(LOCAL_HDFS_PATTERN, context.getDeployDir());

        final File hdfsDirLocal = new File(hdfsDirLocalPath);
        if (hdfsDirLocal.exists()) {

            Path dst = new Path(String.format(REMOTE_HDFS_PATTERN, context.getHdfsPrefix(), context.getWorkflowName()));
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
