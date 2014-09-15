package com.collective.celos.cd.deployer;

import com.collective.celos.cd.config.Config;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import sun.print.resources.serviceui_sv;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;


public class JScpWorker {

    private FileSystemManager fsManager;
    private String userName;
    private String securitySettings;

    public JScpWorker(String userName, String securitySettings) throws FileSystemException {
        this.fsManager = VFS.getManager();
        this.securitySettings = securitySettings;
        this.userName = userName;
    }

    public FileObject getFileObjectByUri(String file) throws URISyntaxException, FileSystemException {
        URI uri = new URI(file);

        if (userName != null) {
            uri = new URI(uri.getScheme(), userName, uri.getHost(), uri.getPort(),
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
        SftpFileSystemConfigBuilder.getInstance().setPreferredAuthentications(opts, securitySettings);
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
        return opts;
    }


}
