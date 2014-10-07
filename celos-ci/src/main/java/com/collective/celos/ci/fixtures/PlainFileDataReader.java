package com.collective.celos.ci.fixtures;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akonopko on 10/7/14.
 */
public class PlainFileDataReader extends AbstractFileDataReader {

    private final File localFile;
    private final File baseLocalDir;

    public PlainFileDataReader(File baseLocalDir, File path) {
        this.localFile = path;
        this.baseLocalDir = baseLocalDir;
    }

    public List<FileInfo> read() throws Exception {

        List<FileInfo> result = Lists.newArrayList();
        java.nio.file.Path pathBase = java.nio.file.Paths.get(baseLocalDir.getAbsolutePath());
        List<File> filesToCopy = localFile.isDirectory() ? findChildren(localFile) : Lists.newArrayList(localFile);
        for (File f : filesToCopy) {
            java.nio.file.Path pathAbsolute = java.nio.file.Paths.get(f.getAbsolutePath());
            java.nio.file.Path pathRelative = pathBase.relativize(pathAbsolute);
            result.add(new FileInfo(getFileContent(f), pathAbsolute.toString(), pathRelative.toString()));
        }
        return result;
    }

    protected InputStream getFileContent(File f) throws FileNotFoundException {
        return new FileInputStream(f);
    }

    protected List<File> findChildren(File file) {
        List<File> result = new ArrayList<>();
        for (File child : file.listFiles()) {
            if (child.isDirectory()) {
                result.addAll(findChildren(child));
            } else {
                result.add(child);
            }
        }
        return result;
    }


}
