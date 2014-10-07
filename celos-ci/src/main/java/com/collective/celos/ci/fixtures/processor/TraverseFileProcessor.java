package com.collective.celos.ci.fixtures.processor;

import com.collective.celos.ci.fixtures.structure.FixDir;
import com.collective.celos.ci.fixtures.structure.FixFile;
import com.collective.celos.ci.fixtures.structure.FixObject;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class TraverseFileProcessor extends AbstractFixObjectProcessor {

    private final Map<Path, FixFile> traversedFiles;
    private final Map<Path, FixDir> traversedLeafDirs;

    public TraverseFileProcessor() {
        this.traversedFiles = Maps.newHashMap();
        this.traversedLeafDirs = Maps.newHashMap();
    }

    @Override
    public void process(Path path, FixObject fo) throws IOException {
        if (fo.isDirectory()) {
            FixDir fd = (FixDir) fo;
            if (isThereChildDirectories(fd)) {
                traversedLeafDirs.put(path, fd);
            }
        } else {
            traversedFiles.put(path, (FixFile) fo);
        }
    }

    private boolean isThereChildDirectories(FixDir fo) {
        FixDir fd = (FixDir) fo;
        for (FixObject foChild : fd.getChildren().values()) {
            if (foChild.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    public Map<Path, FixFile> getTraversedFiles() {
        return traversedFiles;
    }

    public Map<Path, FixDir> getTraversedLeafDirs() {
        return traversedLeafDirs;
    }
}
