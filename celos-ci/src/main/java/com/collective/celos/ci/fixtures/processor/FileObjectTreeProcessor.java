package com.collective.celos.ci.fixtures.processor;

import com.collective.celos.ci.fixtures.structure.FixDir;
import com.collective.celos.ci.fixtures.structure.FixFile;
import com.collective.celos.ci.fixtures.structure.FixObject;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FileObjectTreeProcessor {

    public void process(FixObject object, AbstractFixObjectProcessor processor) throws IOException {
        process(Paths.get(""), object, processor);
    }

    private void process(Path path, FixObject object, AbstractFixObjectProcessor processor) throws IOException {
        if (object.isDirectory()) {
            FixDir fd = (FixDir) object;
            for(Map.Entry<String, FixObject> entry : fd.getChildren().entrySet()) {
                process(Paths.get(path.toString(), entry.getKey()), entry.getValue(), processor);
            }
        } else {
            FixFile ff = (FixFile) object;
            processor.process(path, ff);
        }
    }

}
