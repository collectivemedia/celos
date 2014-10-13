package com.collective.celos.ci.testing.structure.tree;

import java.util.Map;

/**
 * Created by akonopko on 10/9/14.
 */
public interface TreeObject<T extends TreeObject> {

    public Map<String, T> getChildren();

}
