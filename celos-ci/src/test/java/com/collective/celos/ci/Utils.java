package com.collective.celos.ci;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;

/**
 * Created by akonopko on 07.12.14.
 */
public class Utils {

    public static <T extends FixFsObject> FixObjectCreator<T> wrap(final T fixObj) {
        return wrap(fixObj, "");
    }

    public static <T extends FixFsObject> FixObjectCreator<T> wrap(final T fixObj, final String desc) {
        return new FixObjectCreator() {
            @Override
            public FixFsObject create(TestRun testRun) throws Exception {
                return fixObj;
            }

            @Override
            public String getDescription(TestRun testRun) {
                return desc;
            }
        };
    }

}
