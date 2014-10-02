package com.collective.celos.ci.mode;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.mode.test.TestRun;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;

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

        String testCasesDir = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/testrun").getFile();

        CelosCiCommandLine commandLine = new CelosCiCommandLine(targetFile.toURI().toString(), "DEPLOY", "deploydir", "workflow", testCasesDir, "uname");
        TestTask testTask = new TestTask(commandLine);

        Set<String> names = Sets.newHashSet();
        for (TestRun tr : testTask.testRuns) {
            names.add(tr.getTestCaseDir().getName());
        }
        Assert.assertEquals(names, Sets.newHashSet("testcase-1", "testcase-2"));

        Assert.assertEquals(testTask.testRuns.get(0).getCiContext().getTarget().getDefaultsFile(), target.getDefaultsFile());
        Assert.assertEquals(testTask.testRuns.get(0).getCiContext().getTarget().getPathToCoreSite(), target.getPathToCoreSite());
        Assert.assertEquals(testTask.testRuns.get(0).getCiContext().getTarget().getPathToHdfsSite(), target.getPathToHdfsSite());
        Assert.assertTrue(testTask.testRuns.get(0).getCiContext().getTarget().getCelosWorkflowsDirUri().toString().startsWith("file:/tmp/celos"));
        Assert.assertTrue(testTask.testRuns.get(0).getCiContext().getTarget().getCelosWorkflowsDirUri().toString().length() > "file:/tmp/celos".length());

        Assert.assertEquals(testTask.testRuns.get(1).getCiContext().getTarget().getDefaultsFile(), target.getDefaultsFile());
        Assert.assertEquals(testTask.testRuns.get(1).getCiContext().getTarget().getPathToCoreSite(), target.getPathToCoreSite());
        Assert.assertEquals(testTask.testRuns.get(1).getCiContext().getTarget().getPathToHdfsSite(), target.getPathToHdfsSite());
        Assert.assertTrue(testTask.testRuns.get(1).getCiContext().getTarget().getCelosWorkflowsDirUri().toString().startsWith("file:/tmp/celos"));
        Assert.assertTrue(testTask.testRuns.get(1).getCiContext().getTarget().getCelosWorkflowsDirUri().toString().length() > "file:/tmp/celos".length());

    }

}
