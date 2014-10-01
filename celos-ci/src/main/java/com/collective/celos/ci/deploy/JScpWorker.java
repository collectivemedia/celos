package com.collective.celos.ci.deploy;

import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;


public class JScpWorker {

    public static final String DEFAULT_SECURITY_SETTINGS = "gssapi-with-mic,publickey,keyboard-interactive,password";

    private FileSystemManager fsManager;
    private String userName;
    private String securitySettings;

    public JScpWorker(String userName, String securitySettings) throws FileSystemException {
        this.fsManager = VFS.getManager();
        if (securitySettings == null) {
            this.securitySettings = DEFAULT_SECURITY_SETTINGS;
        } else {
            this.securitySettings = securitySettings;
        }
        this.userName = userName;
    }

    public FileObject getFileObjectByUri(String file) throws URISyntaxException, FileSystemException {
        return getFileObjectByUri(URI.create(file));
    }

    public FileObject getFileObjectByUri(URI file) throws URISyntaxException, FileSystemException {
        URI uri = getURIRespectingUsername(file);
        return fsManager.resolveFile(uri.toString(), getSftpDefaultOptions());
    }

    URI getURIRespectingUsername(URI uri) throws URISyntaxException {

        if (userName != null && uri.getUserInfo() == null) {
            uri = new URI(uri.getScheme(), userName, uri.getHost(), uri.getPort(),
                    uri.getPath(), uri.getQuery(),
                    uri.getFragment());
        }
        return uri;
    }

    public FileSystemOptions getSftpDefaultOptions() throws FileSystemException {
        FileSystemOptions opts = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setPreferredAuthentications(opts, securitySettings);
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
        return opts;
    }


}
