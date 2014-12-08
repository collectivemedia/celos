package com.collective.celos.ci.testing;

import java.net.URI;

/**
 * Created by akonopko on 09.12.14.
 */
public class AugumentedHdfsPath {

    private final URI uri;

    public AugumentedHdfsPath(String hdfsPrefix, String original) {
        uri = getUri(hdfsPrefix, URI.create(original));
    }

    private URI getUri(String hdfsPrefix, URI hdfsUri) {
        try {
            return new URI(hdfsUri.getScheme(), hdfsUri.getUserInfo(), hdfsUri.getHost(), hdfsUri.getPort(),
                    hdfsPrefix + hdfsUri.getPath(), hdfsUri.getQuery(),
                    hdfsUri.getFragment());
        } catch (Exception e) {
            return hdfsUri;
        }
    }

    public String getPath() {
        return uri.toString();
    }

}
