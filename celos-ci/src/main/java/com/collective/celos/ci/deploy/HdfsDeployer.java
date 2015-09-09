/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.ci.deploy;

import com.collective.celos.Util;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;

public class HdfsDeployer {

    private static final String REMOTE_HDFS_PATTERN = "%s%s/%s";
    private static final String LOCAL_HDFS_PATTERN = "%s/hdfs";

    private final CelosCiContext context;

    public HdfsDeployer(CelosCiContext context) throws Exception {
        this.context = Util.requireNonNull(context);
    }

    public void undeploy() throws Exception {
        Path dst = getDestinationHdfsPath();
        if (context.getFileSystem().exists(dst)) {
            context.getFileSystem().delete(dst, true);
        }
    }

    Path getDestinationHdfsPath() {
        return new Path(String.format(REMOTE_HDFS_PATTERN, context.getHdfsPrefix(), context.getHdfsRoot(), context.getWorkflowName()));
    }

    public void deploy() throws Exception {

        FileSystem fs = context.getFileSystem();
        final String hdfsDirLocalPath = String.format(LOCAL_HDFS_PATTERN, context.getDeployDir());

        final File hdfsDirLocal = new File(hdfsDirLocalPath);
        if (!hdfsDirLocal.exists()) {
            throw new IllegalStateException(hdfsDirLocalPath + " not found local FS");
        }

        undeploy();

        Path dst = getDestinationHdfsPath();
        fs.mkdirs(dst);
        String[] childFiles = hdfsDirLocal.list();
        for (String child : childFiles) {
            fs.copyFromLocalFile(new Path(hdfsDirLocalPath, child), dst);
        }
    }

}
