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


public class WorkflowFilesDeployerTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testGetWorkflowJsUri1() throws FileSystemException, URISyntaxException {
        CelosCiContext context = mock(CelosCiContext.class);
        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), null, null, URI.create("hiveJdbc"));
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();
        Assert.assertEquals(deployer.getTargetJsFileUri(URI.create("")), URI.create("/workflow.js"));
    }

    @Test
    public void testGetWorkflowJsUri2() throws FileSystemException, URISyntaxException {
        CelosCiContext context = mock(CelosCiContext.class);
        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        doReturn("workflow").when(context).getWorkflowName();
        Assert.assertEquals(deployer.getTargetJsFileUri(URI.create("/home")), URI.create("/home/workflow.js"));
    }

    @Test
    public void testGetWorkflowJsUri3() throws FileSystemException, URISyntaxException {
        CelosCiContext context = mock(CelosCiContext.class);
        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        doReturn("workflow").when(context).getWorkflowName();
        Assert.assertEquals(deployer.getTargetJsFileUri(URI.create("sftp://user@server:999/home")), URI.create("sftp://user@server:999/home/workflow.js"));
    }

    @Test
    public void testGetWorkflowJsUri4() throws FileSystemException, URISyntaxException {
        CelosCiContext context = mock(CelosCiContext.class);
        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        doReturn("workflow").when(context).getWorkflowName();
        Assert.assertEquals(deployer.getTargetJsFileUri(null), null);
    }

    @Test
    public void testDeployWorkflowFile() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File localFolder = tempDir.newFolder();
        localFolder.mkdirs();
        File wfFile = new File(localFolder, "workflow.js");
        wfFile.createNewFile();

        OutputStream os = new FileOutputStream(wfFile);
        os.write("newfile".getBytes());
        os.flush();

        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        File remoteFolderWf = tempDir.newFolder();
        remoteFolderWf.mkdirs();

        File remoteFolderDef = tempDir.newFolder();
        remoteFolderDef.mkdirs();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), remoteFolderWf.toURI(), remoteFolderDef.toURI(), URI.create("hiveJdbc"));

        doReturn(localFolder).when(context).getDeployDir();
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();

        deployer.deploy();

        Assert.assertArrayEquals(remoteFolderWf.list(), new String[] {"workflow.js"});
        Assert.assertArrayEquals(remoteFolderDef.list(), new String[] {});
    }

    @Test
    public void testDeployDefaultsFile() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File localFolder = tempDir.newFolder();
        localFolder.mkdirs();
        File defFile = new File(localFolder, "defaults.js");
        defFile.createNewFile();

        OutputStream os = new FileOutputStream(defFile);
        os.write("newfile".getBytes());
        os.flush();

        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        File remoteFolderWf = tempDir.newFolder();
        remoteFolderWf.mkdirs();

        File remoteFolderDef = tempDir.newFolder();
        remoteFolderDef.mkdirs();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), remoteFolderWf.toURI(), remoteFolderDef.toURI(), URI.create("hiveJdbc"));

        doReturn(localFolder).when(context).getDeployDir();
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();

        deployer.deploy();

        Assert.assertArrayEquals(remoteFolderWf.list(), new String[] {});
        Assert.assertArrayEquals(remoteFolderDef.list(), new String[] {"workflow.js"});
    }


    @Test
    public void testDeployWorkflowFileNullDefDirURI() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File localFolder = tempDir.newFolder();
        localFolder.mkdirs();
        File wfFile = new File(localFolder, "workflow.js");
        wfFile.createNewFile();

        OutputStream os = new FileOutputStream(wfFile);
        os.write("newfile".getBytes());
        os.flush();

        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        File remoteFolderWf = tempDir.newFolder();
        remoteFolderWf.mkdirs();

        File remoteFolderDef = tempDir.newFolder();
        remoteFolderDef.mkdirs();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), remoteFolderWf.toURI(), null, URI.create("hiveJdbc"));

        doReturn(localFolder).when(context).getDeployDir();
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();

        deployer.deploy();

        Assert.assertArrayEquals(remoteFolderWf.list(), new String[] {"workflow.js"});
        Assert.assertArrayEquals(remoteFolderDef.list(), new String[] {});
    }

    @Test
    public void testDeployDefaultsFileNullWFDirURI() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File localFolder = tempDir.newFolder();
        localFolder.mkdirs();
        File defFile = new File(localFolder, "defaults.js");
        defFile.createNewFile();

        OutputStream os = new FileOutputStream(defFile);
        os.write("newfile".getBytes());
        os.flush();

        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        File remoteFolderWf = tempDir.newFolder();
        remoteFolderWf.mkdirs();

        File remoteFolderDef = tempDir.newFolder();
        remoteFolderDef.mkdirs();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), null, remoteFolderDef.toURI(), URI.create("hiveJdbc"));

        doReturn(localFolder).when(context).getDeployDir();
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();

        deployer.deploy();

        Assert.assertArrayEquals(remoteFolderWf.list(), new String[] {});
        Assert.assertArrayEquals(remoteFolderDef.list(), new String[] {"workflow.js"});
    }

    @Test
    public void testDeployDefaultsAndWorkflowFile() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File localFolder = tempDir.newFolder();
        localFolder.mkdirs();

        File wfFile = new File(localFolder, "workflow.js");
        wfFile.createNewFile();

        File defFile = new File(localFolder, "defaults.js");
        defFile.createNewFile();

        OutputStream os1 = new FileOutputStream(defFile);
        os1.write("newfile".getBytes());
        os1.flush();

        OutputStream os2 = new FileOutputStream(wfFile);
        os2.write("newfile".getBytes());
        os2.flush();

        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        File remoteFolderWf = tempDir.newFolder();
        remoteFolderWf.mkdirs();

        File remoteFolderDef = tempDir.newFolder();
        remoteFolderDef.mkdirs();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), remoteFolderWf.toURI(), remoteFolderDef.toURI(), URI.create("hiveJdbc"));
        doReturn(localFolder).when(context).getDeployDir();
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();

        deployer.deploy();

        Assert.assertArrayEquals(remoteFolderWf.list(), new String[] {"workflow.js"});
        Assert.assertArrayEquals(remoteFolderDef.list(), new String[] {"workflow.js"});
    }

    @Test
    public void testUndeployWf() throws IOException, URISyntaxException {
        CelosCiContext context = mock(CelosCiContext.class);

        File remoteFolderWf = tempDir.newFolder();
        remoteFolderWf.mkdirs();
        File wfFile = new File(remoteFolderWf, "workflow.js");
        wfFile.createNewFile();

        File remoteFolderDef = tempDir.newFolder();
        remoteFolderDef.mkdirs();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), remoteFolderWf.toURI(), remoteFolderDef.toURI(), URI.create("hiveJdbc"));
        doReturn(remoteFolderDef).when(context).getDeployDir();
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();

        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        Assert.assertTrue(wfFile.exists());

        deployer.undeploy();

        Assert.assertFalse(wfFile.exists());
    }

    @Test
    public void testUndeployDef() throws IOException, URISyntaxException {
        CelosCiContext context = mock(CelosCiContext.class);

        File remoteFolderWf = tempDir.newFolder();
        remoteFolderWf.mkdirs();

        File remoteFolderDef = tempDir.newFolder();
        remoteFolderDef.mkdirs();
        File defFile = new File(remoteFolderDef, "workflow.js");
        defFile.createNewFile();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), remoteFolderWf.toURI(), remoteFolderDef.toURI(), URI.create("hiveJdbc"));
        doReturn(remoteFolderDef).when(context).getDeployDir();
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();

        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        Assert.assertTrue(defFile.exists());

        deployer.undeploy();

        Assert.assertFalse(defFile.exists());
    }

    @Test
    public void testUndeployBoth() throws IOException, URISyntaxException {
        CelosCiContext context = mock(CelosCiContext.class);

        File remoteFolderWf = tempDir.newFolder();
        remoteFolderWf.mkdirs();
        File wfFile = new File(remoteFolderWf, "workflow.js");
        wfFile.createNewFile();

        File remoteFolderDef = tempDir.newFolder();
        remoteFolderDef.mkdirs();
        File defFile = new File(remoteFolderDef, "workflow.js");
        defFile.createNewFile();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), remoteFolderWf.toURI(), remoteFolderDef.toURI(), URI.create("hiveJdbc"));
        doReturn(remoteFolderDef).when(context).getDeployDir();
        doReturn(target).when(context).getTarget();
        doReturn("workflow").when(context).getWorkflowName();

        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        Assert.assertTrue(wfFile.exists());
        Assert.assertTrue(defFile.exists());

        deployer.undeploy();

        Assert.assertFalse(wfFile.exists());
        Assert.assertFalse(defFile.exists());
    }


    @Test
    public void testDeployFileExists() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File localFolder = tempDir.newFolder();
        localFolder.mkdirs();
        File wfFile = new File(localFolder, "workflow.js");
        wfFile.createNewFile();

        writeTextToFile(wfFile, "newfile");
        WorkflowFilesDeployer deployer = new WorkflowFilesDeployer(context);

        File remoteFolder = tempDir.newFolder();
        remoteFolder.mkdirs();

        CelosCiTarget target = new CelosCiTarget(URI.create(""), URI.create(""), remoteFolder.toURI(), null, URI.create("hiveJdbc"));
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
