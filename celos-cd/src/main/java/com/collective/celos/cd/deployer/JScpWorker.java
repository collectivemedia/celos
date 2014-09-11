package com.collective.celos.cd.deployer;

import com.collective.celos.config.Config;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;


public class JScpWorker {

    private Config config;
    private FileSystemManager fsManager;

    public JScpWorker(Config config) throws FileSystemException {
        this.config = config;
        this.fsManager = VFS.getManager();
    }

    public FileObject getFileObjectByUri(String file) throws URISyntaxException, FileSystemException {
        URI uri = new URI(file);

        if (config.getUserName() != null) {
            uri = new URI(uri.getScheme(),
                    config.getUserName(), uri.getHost(), uri.getPort(),
                    uri.getPath(), uri.getQuery(),
                    uri.getFragment());
        }
        return fsManager.resolveFile(uri.toString(), getSftpDefaultOptions());
    }

    public void copyFileToRemote(File localFile, FileObject remoteFile) throws FileSystemException {
        FileObject localFileObject = fsManager.resolveFile(localFile.getAbsolutePath());
        remoteFile.copyFrom(localFileObject, Selectors.SELECT_SELF);
    }

    public InputStream getRemoteFileIS(String uri) throws FileSystemException, URISyntaxException {
        FileObject file = getFileObjectByUri(uri);
        FileContent content = file.getContent();
        return content.getInputStream();
    }

    public FileSystemOptions getSftpDefaultOptions() throws FileSystemException {
        FileSystemOptions opts = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setPreferredAuthentications(opts, config.getScpSecuritySettings());
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
        return opts;
    }


}
