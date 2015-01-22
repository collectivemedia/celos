package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;

/**
 * Created by akonopko on 10/7/14.
 */
public class ConvertionCreator<S extends FixObject, T extends FixObject> implements FixObjectCreator<T> {

    protected final FixObjectCreator<S> creator;
    protected final AbstractFixObjectConverter<S, T> fixObjectConverter;

    public ConvertionCreator(FixObjectCreator<S> creator, AbstractFixObjectConverter<S, T> fixObjectConverter) {
        this.creator = creator;
        this.fixObjectConverter = fixObjectConverter;
    }

    @Override
    public T create(TestRun testRun) throws Exception {
        return fixObjectConverter.convert(testRun, creator.create(testRun));
    }

    @Override
    public String getDescription(TestRun testRun) {
        return creator.getDescription(testRun);
    }

    public FixObjectCreator getCreator() {
        return creator;
    }

    public AbstractFixObjectConverter getFixObjectConverter() {
        return fixObjectConverter;
    }
}
