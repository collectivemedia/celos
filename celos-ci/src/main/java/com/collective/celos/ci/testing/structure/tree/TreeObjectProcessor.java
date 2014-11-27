package com.collective.celos.ci.testing.structure.tree;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class TreeObjectProcessor<T extends TreeObject> {

    public static void process(TreeObject object, TreeObjectProcessor processor) throws IOException {
        process(Paths.get(""), object, processor);
    }

    private static void process(Path path, TreeObject object, TreeObjectProcessor processor) throws IOException {
        processor.process(path, object);
        Map<String, TreeObject> map = object.getChildren();
        for(Map.Entry<String, TreeObject> entry : map.entrySet()) {
            process(Paths.get(path.toString(), entry.getKey()), entry.getValue(), processor);
        }
    }


    public abstract void process(Path path, T ff) throws IOException;

}
