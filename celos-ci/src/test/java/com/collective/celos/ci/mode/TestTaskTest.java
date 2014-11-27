package com.collective.celos.ci.mode;

import com.collective.celos.ScheduledTime;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveDirComparer;
import com.collective.celos.ci.testing.fixtures.create.FixDirFromHdfsCreator;
import com.collective.celos.ci.testing.fixtures.create.FixDirFromResourceCreator;
import com.collective.celos.ci.testing.fixtures.deploy.HdfsInputDeployer;
import junit.framework.Assert;
import org.apache.hadoop.fs.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by akonopko on 10/2/14.
 */
public class TestTaskTest {


    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testTestRunsAreCreatedOk() throws Exception {

        String hadoopCoreUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/core-site.xml").getFile();
        String hadoopHdfsUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/hdfs-site.xml").getFile();

        String targetFileStr = "{\n" +
                "    \"security.settings\": \"secsettings\",\n" +
                "    \"celos.workflow.dir\": \"celoswfdir\",\n" +
                "    \"hadoop.hdfs-site.xml\": \"" + hadoopHdfsUrl +"\",\n" +
                "    \"hadoop.core-site.xml\": \"" + hadoopCoreUrl +"\",\n" +
                "    \"defaults.file.uri\": \"deffile\"\n" +
                "}\n";

        File targetFile = tempDir.newFile();
        FileOutputStream stream = new FileOutputStream(targetFile);
        stream.write(targetFileStr.getBytes());
        stream.flush();

        CelosCiTargetParser parser = new CelosCiTargetParser("");
        CelosCiTarget target = parser.parse(targetFile.toURI());

        String tmpDir = new File(System.getProperty("java.io.tmpdir")).toURI().toString() + "celos";
        
        String configJS = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/test.js").getFile();

        CelosCiCommandLine commandLine = new CelosCiCommandLine(targetFile.toURI().toString(), "DEPLOY", "deploydir", "workflow", "testDir", "uname");
        TestTask testTask = new TestTask(commandLine, new File(configJS));

        Assert.assertEquals(testTask.getTestRuns().get(0).getCiContext().getTarget().getDefaultsFile(), target.getDefaultsFile());
        Assert.assertEquals(testTask.getTestRuns().get(0).getCiContext().getTarget().getPathToCoreSite(), target.getPathToCoreSite());
        Assert.assertEquals(testTask.getTestRuns().get(0).getCiContext().getTarget().getPathToHdfsSite(), target.getPathToHdfsSite());
        Assert.assertTrue(testTask.getTestRuns().get(0).getCiContext().getTarget().getCelosWorkflowsDirUri().toString().startsWith(tmpDir));
        Assert.assertTrue(testTask.getTestRuns().get(0).getCiContext().getTarget().getCelosWorkflowsDirUri().toString().length() > tmpDir.length());
        Assert.assertEquals(testTask.getTestRuns().get(0).getTestCase().getName(), "wordcount test case 1");
        Assert.assertEquals(testTask.getTestRuns().get(0).getTestCase().getSampleTimeStart(), new ScheduledTime("2013-11-20T11:00Z"));
        Assert.assertEquals(testTask.getTestRuns().get(0).getTestCase().getSampleTimeEnd(), new ScheduledTime("2013-11-20T18:00Z"));
        HdfsInputDeployer deployer1 = (HdfsInputDeployer) testTask.getTestRuns().get(0).getTestCase().getInputs().get(0);
        HdfsInputDeployer deployer2 = (HdfsInputDeployer) testTask.getTestRuns().get(0).getTestCase().getInputs().get(1);
        Assert.assertEquals(deployer1.getPath(), new Path("input/wordcount1"));
        Assert.assertEquals(deployer2.getPath(), new Path("input/wordcount11"));
        RecursiveDirComparer comparer = (RecursiveDirComparer) testTask.getTestRuns().get(0).getTestCase().getOutputs().get(0);
        FixDirFromHdfsCreator hdfsCreator = (FixDirFromHdfsCreator) comparer.getActualDataCreator();
        Assert.assertEquals(hdfsCreator.getPath(), new Path("output/wordcount1"));
        FixDirFromResourceCreator resourceDataCreator = (FixDirFromResourceCreator) comparer.getExpectedDataCreator();
        Assert.assertEquals(resourceDataCreator.getPath(), new File("testDir/src/test/celos-ci/test-1/output/plain/output/wordcount1"));

        Assert.assertEquals(testTask.getTestRuns().get(1).getCiContext().getTarget().getDefaultsFile(), target.getDefaultsFile());
        Assert.assertEquals(testTask.getTestRuns().get(1).getCiContext().getTarget().getPathToCoreSite(), target.getPathToCoreSite());
        Assert.assertEquals(testTask.getTestRuns().get(1).getCiContext().getTarget().getPathToHdfsSite(), target.getPathToHdfsSite());
        Assert.assertTrue(testTask.getTestRuns().get(1).getCiContext().getTarget().getCelosWorkflowsDirUri().toString().startsWith(tmpDir));
        Assert.assertTrue(testTask.getTestRuns().get(1).getCiContext().getTarget().getCelosWorkflowsDirUri().toString().length() > tmpDir.length());
        Assert.assertEquals(testTask.getTestRuns().get(1).getTestCase().getName(), "wordcount test case 2");
        Assert.assertEquals(testTask.getTestRuns().get(1).getTestCase().getSampleTimeStart(), new ScheduledTime("2013-12-20T16:00Z"));
        Assert.assertEquals(testTask.getTestRuns().get(1).getTestCase().getSampleTimeEnd(), new ScheduledTime("2013-12-20T18:00Z"));
        HdfsInputDeployer deployer21 = (HdfsInputDeployer) testTask.getTestRuns().get(1).getTestCase().getInputs().get(0);
        Assert.assertEquals(deployer21.getPath(), new Path("input/wordcount2"));
        RecursiveDirComparer comparer2 = (RecursiveDirComparer) testTask.getTestRuns().get(1).getTestCase().getOutputs().get(0);
        FixDirFromHdfsCreator hdfsCreator2 = (FixDirFromHdfsCreator) comparer2.getActualDataCreator();
        Assert.assertEquals(hdfsCreator2.getPath(), new Path("output/wordcount2"));
        FixDirFromResourceCreator resourceDataCreator2 = (FixDirFromResourceCreator) comparer2.getExpectedDataCreator();
        Assert.assertEquals(resourceDataCreator2.getPath(), new File("testDir/src/test/celos-ci/test-1/output/plain/output/wordcount2"));

    }

}
