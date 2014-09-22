package com.collective.celos.cd.deployer;

import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;


public class JScpWorker {

    public static final String DEFAULT_SECURITY_SETTINGS = "gssapi-with-mic,publickey,keyboard-interactive,password";
    public static final int TIMEOUT_MS = 5000;

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
        URI uri = new URI(file);

        if (userName != null && uri.getUserInfo() == null) {
            uri = new URI(uri.getScheme(), userName, uri.getHost(), uri.getPort(),
                    uri.getPath(), uri.getQuery(),
                    uri.getFragment());
        }
        return fsManager.resolveFile(uri.toString(), getSftpDefaultOptions());
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
        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, TIMEOUT_MS);
        return opts;
    }


}
