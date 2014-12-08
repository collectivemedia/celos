package com.collective.celos.ci;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;

/**
 * Created by akonopko on 07.12.14.
 */
public class Utils {

    public static <T extends FixObject> FixObjectCreator<T> wrap(final FixObject fixObj) {
        return wrap(fixObj, "");
    }

    public static <T extends FixObject> FixObjectCreator<T> wrap(final FixObject fixObj, final String desc) {
        return new FixObjectCreator() {
            @Override
            public FixObject create(TestRun testRun) throws Exception {
                return fixObj;
            }

            @Override
            public String getDescription(TestRun testRun) {
                return desc;
            }
        };
    }

}
