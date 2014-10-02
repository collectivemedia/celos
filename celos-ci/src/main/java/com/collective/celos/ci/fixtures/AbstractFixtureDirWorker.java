package com.collective.celos.ci.fixtures;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akonopko on 9/18/14.
 */
public abstract class AbstractFixtureDirWorker extends AbstractFixtureFileWorker {

    @Override
    protected List<File> findFiles(File dir) {
        List<File> result = new ArrayList<>();
        for (File child : dir.listFiles()) {
            if (child.isDirectory()) {
                if (hasLeafDirs(child)) {
                    result.addAll(findFiles(child));
                } else {
                    result.add(child);
                }
            }
        }
        return result;
    }

    private boolean hasLeafDirs(File dir) {
        File[] list = dir.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                return true;
            }
        }
        return false;
    }

}
