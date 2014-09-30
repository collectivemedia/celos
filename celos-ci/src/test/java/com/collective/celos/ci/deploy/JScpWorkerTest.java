package com.collective.celos.ci.deploy;

import junit.framework.Assert;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;


public class JScpWorkerTest {

    @Test
    public void getURIRespectingUsernameChanges() throws FileSystemException, URISyntaxException {
        JScpWorker worker = new JScpWorker("uname", JScpWorker.DEFAULT_SECURITY_SETTINGS);

        URI uri = worker.getURIRespectingUsername("sftp://server/path1/path2");
        Assert.assertEquals(uri.toString(), "sftp://uname@server/path1/path2");
    }

    @Test
    public void getURIRespectingUsernameDoesntChange1() throws FileSystemException, URISyntaxException {
        JScpWorker worker = new JScpWorker("uname", JScpWorker.DEFAULT_SECURITY_SETTINGS);

        URI uri = worker.getURIRespectingUsername("sftp://user@server/path1/path2");
        Assert.assertEquals(uri.toString(), "sftp://user@server/path1/path2");
    }

    @Test
    public void getURIRespectingUsernameDoesntChange2() throws FileSystemException, URISyntaxException {
        JScpWorker worker = new JScpWorker("uname", JScpWorker.DEFAULT_SECURITY_SETTINGS);

        URI uri = worker.getURIRespectingUsername("sftp://user@server/path1/path2");
        Assert.assertEquals(uri.toString(), "sftp://user@server/path1/path2");
    }

}
