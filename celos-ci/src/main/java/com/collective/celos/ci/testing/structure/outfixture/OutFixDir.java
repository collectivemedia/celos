package com.collective.celos.ci.testing.structure.outfixture;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.tree.TreeNodeObject;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class OutFixDir extends FixDir implements OutFixObject {

    private final FixObjectComparer comparer;

    public OutFixDir(Map<String, OutFixObject> content, FixObjectComparer comparer) {
        super(content);
        this.comparer = comparer;
    }

    public FixObjectCompareResult compare(FixObject other) throws Exception {
        return comparer.compare(this, other);
    }

    public FixObjectComparer getComparer() {
        return comparer;
    }

}
