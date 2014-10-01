package com.collective.celos.ci.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


public class WorkflowFileDeployerTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test(expected = IllegalStateException.class)
    public void testDeployFailsFileNotFound() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File localFolder = tempDir.newFolder();

        WorkflowFileDeployer deployer = new WorkflowFileDeployer(context);
        doReturn(localFolder).when(context).getDeployDir();
        deployer.deploy();
    }

    @Test
    public void testGetWorkflowJsUri1() throws FileSystemException, URISyntaxException {
        CelosCiContext context = mock(CelosCiContext.class);
        WorkflowFileDeployer deployer = new WorkflowFileDeployer(context);

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), URI.create(""), URI.create(""));
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();
        Assert.assertEquals(deployer.getWorkflowJsUri(), URI.create("/workflow.js"));
    }

    @Test
    public void testGetWorkflowJsUri2() throws FileSystemException, URISyntaxException {
        CelosCiContext context = mock(CelosCiContext.class);
        WorkflowFileDeployer deployer = new WorkflowFileDeployer(context);

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), URI.create("/home"), URI.create(""));
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();
        Assert.assertEquals(deployer.getWorkflowJsUri(), URI.create("/home/workflow.js"));
    }

    @Test
    public void testGetWorkflowJsUri3() throws FileSystemException, URISyntaxException {
        CelosCiContext context = mock(CelosCiContext.class);
        WorkflowFileDeployer deployer = new WorkflowFileDeployer(context);

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), URI.create("sftp://user@server:999/home"), URI.create(""));
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();
        Assert.assertEquals(deployer.getWorkflowJsUri(), URI.create("sftp://user@server:999/home/workflow.js"));
    }

    @Test
    public void testDeploy() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File localFolder = tempDir.newFolder();
        localFolder.mkdirs();
        File wfFile = new File(localFolder, "workflow.js");
        wfFile.createNewFile();

        OutputStream os = new FileOutputStream(wfFile);
        os.write("newfile".getBytes());
        os.flush();

        WorkflowFileDeployer deployer = new WorkflowFileDeployer(context);

        File remoteFolder = tempDir.newFolder();
        remoteFolder.mkdirs();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), remoteFolder.toURI(), URI.create(""));
        doReturn(localFolder).when(context).getDeployDir();
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();

        deployer.deploy();

        Assert.assertArrayEquals(remoteFolder.list(), new String[] {"workflow.js"});
    }

    @Test
    public void testUndeploy() throws IOException, URISyntaxException {
        CelosCiContext context = mock(CelosCiContext.class);

        File remoteFolder = tempDir.newFolder();
        remoteFolder.mkdirs();
        File wfFile = new File(remoteFolder, "workflow.js");
        wfFile.createNewFile();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), remoteFolder.toURI(), URI.create(""));
        doReturn(remoteFolder).when(context).getDeployDir();
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();

        WorkflowFileDeployer deployer = new WorkflowFileDeployer(context);

        Assert.assertTrue(wfFile.exists());
        deployer.undeploy();
        Assert.assertFalse(wfFile.exists());
    }


    @Test
    public void testDeployFileExists() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File localFolder = tempDir.newFolder();
        localFolder.mkdirs();
        File wfFile = new File(localFolder, "workflow.js");
        wfFile.createNewFile();

        writeTextToFile(wfFile, "newfile");
        WorkflowFileDeployer deployer = new WorkflowFileDeployer(context);

        File remoteFolder = tempDir.newFolder();
        remoteFolder.mkdirs();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), remoteFolder.toURI(), URI.create(""));
        doReturn(localFolder).when(context).getDeployDir();
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();

        File destFile = new File(remoteFolder, "workflow.js");
        destFile.createNewFile();
        writeTextToFile(destFile, "oldfile");

        Assert.assertEquals("oldfile", readFileContent(destFile));

        deployer.deploy();

        Assert.assertArrayEquals(remoteFolder.list(), new String[] {"workflow.js"});
        Assert.assertEquals("newfile", readFileContent(destFile));
    }

    private String  readFileContent(File fileExists) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(fileExists.toURI()));
        return new String(encoded);
    }

    private void writeTextToFile(File wfFile, String test) throws IOException {
        OutputStream os = new FileOutputStream(wfFile);
        os.write(test.getBytes());
        os.flush();
        os.close();
    }


}
