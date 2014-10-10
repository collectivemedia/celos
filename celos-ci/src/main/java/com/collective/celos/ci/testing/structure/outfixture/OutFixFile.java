package com.collective.celos.ci.testing.structure.outfixture;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import java.io.InputStream;

/**
 * Created by akonopko on 10/7/14.
 */
public class OutFixFile extends FixFile implements OutFixObject {

    private final FixObjectComparer comparer;

    public OutFixFile(InputStream inputStream, FixObjectComparer comparer) {
        super(inputStream);
        this.comparer = comparer;
    }

    public FixObjectCompareResult compare(FixObject other) throws Exception {
        return comparer.compare(this, other);
    }

    public FixObjectComparer getComparer() {
        return comparer;
    }

}
