package com.collective.celos.ci.mode.test;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.testing.TestConfigParser;
import com.collective.celos.ci.config.testing.TestContext;
import com.collective.celos.ci.deploy.HdfsDeployer;
import com.collective.celos.ci.deploy.JScpWorker;
import com.collective.celos.ci.deploy.WorkflowFileDeployer;
import com.collective.celos.ci.fixtures.AbstractFixtureFileWorker;
import com.collective.celos.ci.fixtures.FixturesHdfsWorkerManager;
import com.collective.celos.ci.fixtures.compare.PlainFixtureComparatorWorker;
import com.collective.celos.ci.fixtures.deploy.PlainFixtureDeployWorker;
import com.collective.celos.server.CelosServer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class CelosCiIntegrationTestRunner {


    public void runTests() throws Exception {
    }



}

