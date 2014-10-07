package com.collective.celos.ci.fixtures;

import java.io.InputStream;

/**
 * Created by akonopko on 10/7/14.
 */
public class FileInfo {

    private final InputStream inputStream;
    private final String fullPath;
    private final String relativePath;

    public FileInfo(InputStream inputStream, String fullPath, String relativePath) {
        this.inputStream = inputStream;
        this.fullPath = fullPath;
        this.relativePath = relativePath;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getRelativePath() {
        return relativePath;
    }
}
