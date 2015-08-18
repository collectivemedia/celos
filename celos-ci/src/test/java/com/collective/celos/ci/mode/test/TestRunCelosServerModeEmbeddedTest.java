package com.collective.celos.ci.mode.test;

import com.collective.celos.ci.config.CiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by akonopko on 19.08.15.
 */
public class TestRunCelosServerModeEmbeddedTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    public File tmpDirFile;

    @Before
    public void before() throws IOException {
        tmpDirFile = tempDir.newFolder();
    }


    @Test
    public void testCopyRemoteDefaults() throws Exception {

        File deployDir = tempDir.newFolder();
        deployDir.mkdirs();
        File defFile = new File(deployDir, "defaults.js");
        defFile.createNewFile();

        File remoteFolderWf = tempDir.newFolder();
        remoteFolderWf.mkdirs();

        File remoteFolderDef = tempDir.newFolder();
        remoteFolderDef.mkdirs();
        File otherDefFile1 = new File(remoteFolderDef, "some-defaults1.js");
        otherDefFile1.createNewFile();
        File otherDefFile2 = new File(remoteFolderDef, "some-defaults2.js");
        otherDefFile2.createNewFile();

        CiCommandLine commandLine = new CiCommandLine("", "TEST", deployDir.getAbsolutePath(), "myworkflow", tmpDirFile.getAbsolutePath(), "uname", false, null);

        TestRunCelosServerModeEmbedded modeEmbedded = new TestRunCelosServerModeEmbedded(commandLine, tmpDirFile, UUID.randomUUID());

        modeEmbedded.copyRemoteDefaultsToLocal("uname", remoteFolderDef.toURI());

        String[] fileNames = new File(modeEmbedded.getCelosDefaultsDir()).list();
        Arrays.sort(fileNames);

        Assert.assertArrayEquals(fileNames, new String[]{"some-defaults1.js", "some-defaults2.js"});
    }

    @Test
    public void testCopyRemoteNoDefaults() throws Exception {

        File deployDir = tempDir.newFolder();
        deployDir.mkdirs();
        File defFile = new File(deployDir, "defaults.js");
        defFile.createNewFile();

        File remoteFolderWf = tempDir.newFolder();
        remoteFolderWf.mkdirs();

        File remoteFolderDef = tempDir.newFolder();
        remoteFolderDef.mkdirs();

        CiCommandLine commandLine = new CiCommandLine("", "TEST", deployDir.getAbsolutePath(), "myworkflow", tmpDirFile.getAbsolutePath(), "uname", false, null);

        TestRunCelosServerModeEmbedded modeEmbedded = new TestRunCelosServerModeEmbedded(commandLine, tmpDirFile, UUID.randomUUID());

        modeEmbedded.copyRemoteDefaultsToLocal("uname", remoteFolderDef.toURI());

        String[] fileNames = new File(modeEmbedded.getCelosDefaultsDir()).list();

        Assert.assertNull(fileNames);
    }

}
