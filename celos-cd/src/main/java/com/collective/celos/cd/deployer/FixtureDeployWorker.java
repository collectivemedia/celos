package com.collective.celos.cd.deployer;

import org.apache.hadoop.fs.FileSystem;

import java.io.File;

/**
 * Created by akonopko on 9/18/14.
 */
public interface FixtureDeployWorker {

    public void deploy(File localDir, FileSystem fs) throws Exception;
}
