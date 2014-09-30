package com.collective.celos.ci.fixtures;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akonopko on 9/18/14.
 */
public abstract class AbstractFixtureDirWorker extends AbstractFixtureFileWorker {

    @Override
    protected List<File> findFiles(File file) {
        List<File> result = new ArrayList<>();
        for (File child : file.listFiles()) {
            if (child.isDirectory()) {
                if (isNotEmptyDir(child)) {
                    result.addAll(findFiles(child));
                } else {
                    result.add(child);
                }
            }
        }
        return result;
    }

    private boolean isNotEmptyDir(File file) {
        String[] list = file.list();
        return list != null && list.length > 0;
    }

}
