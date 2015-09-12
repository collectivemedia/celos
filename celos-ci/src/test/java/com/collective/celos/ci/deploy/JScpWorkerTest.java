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

import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class JScpWorkerTest {

    @Test
    public void getURIRespectingUsernameChanges() throws FileSystemException, URISyntaxException {
        JScpWorker worker = new JScpWorker("uname");

        URI uri = worker.getURIRespectingUsername(URI.create("sftp://server/path1/path2"));
        Assert.assertEquals(uri, URI.create("sftp://uname@server/path1/path2"));
    }

    @Test
    public void getURIRespectingUsernameDoesntChange1() throws FileSystemException, URISyntaxException {
        JScpWorker worker = new JScpWorker("uname");

        URI uri = worker.getURIRespectingUsername(URI.create("sftp://user@server/path1/path2"));
        Assert.assertEquals(uri, URI.create("sftp://user@server/path1/path2"));
    }

    @Test
    public void getURIRespectingUsernameDoesntChange2() throws FileSystemException, URISyntaxException {
        JScpWorker worker = new JScpWorker("uname");

        URI uri = worker.getURIRespectingUsername(URI.create("sftp://user@server/path1/path2"));
        Assert.assertEquals(uri, URI.create("sftp://user@server/path1/path2"));
    }

    @Test
    public void testGetFileObjectByUri() throws Exception {
        JScpWorker worker = new JScpWorker("uname");
        URL res = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/target.json");
        FileObject object = worker.getFileObjectByUri(res.toURI());
        IOUtils.contentEquals(object.getContent().getInputStream(), new FileInputStream(res.getFile()));
    }

    @Test
    public void testGetFileObjectByUriStringParam() throws Exception {
        JScpWorker worker = new JScpWorker("uname");
        URL res = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/target.json");
        FileObject object = worker.getFileObjectByUri(res.toURI().toString());
        IOUtils.contentEquals(object.getContent().getInputStream(), new FileInputStream(res.getFile()));
    }

}
