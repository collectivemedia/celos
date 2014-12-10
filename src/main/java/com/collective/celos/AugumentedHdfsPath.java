package com.collective.celos;

import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by akonopko on 09.12.14.
 */
public class AugumentedHdfsPath {

    private static Map<String, String> converstionBeforeParse = ImmutableMap.of(
            "$", Character.toString((char) 0xE000),
            "{", Character.toString((char) 0xE001),
            "}", Character.toString((char) 0xE002));


    private static Map<String, String> converstionAfterParse = ImmutableMap.of(
            Character.toString((char) 0xE000), "$",
            Character.toString((char) 0xE001), "{",
            Character.toString((char) 0xE002), "}");

    private final String augumentedPath;

    public AugumentedHdfsPath(String hdfsPrefix, String path) throws URISyntaxException {

        for (String ch : converstionBeforeParse.keySet()) {
            path = path.replace(ch.toString(), converstionBeforeParse.get(ch).toString());
        }
        URI oldUri = URI.create(path);

        String host = oldUri.getHost();
        if (oldUri.getRawSchemeSpecificPart().startsWith("///") && host == null) {
            host = "";
        }

        URI newUri = new URI(oldUri.getScheme(), oldUri.getUserInfo(), host, oldUri.getPort(), hdfsPrefix + oldUri.getPath(), oldUri.getQuery(), oldUri.getFragment());
        path = newUri.toString();
        for (String ch : converstionAfterParse.keySet()) {
            path = path.replace(ch.toString(), converstionAfterParse.get(ch).toString());
        }
        augumentedPath = path;
    }

    public String getPath() {
        return augumentedPath;
    }

}
