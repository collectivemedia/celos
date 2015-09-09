/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;

/**
 * Created by akonopko on 10/7/14.
 */
public class ConversionCreator<S extends FixObject, T extends FixObject> implements FixObjectCreator<T> {

    protected final FixObjectCreator<S> creator;
    protected final AbstractFixObjectConverter<S, T> fixObjectConverter;

    public ConversionCreator(FixObjectCreator<S> creator, AbstractFixObjectConverter<S, T> fixObjectConverter) {
        this.creator = creator;
        this.fixObjectConverter = fixObjectConverter;
    }

    @Override
    public T create(TestRun testRun) throws Exception {
        return fixObjectConverter.convert(testRun, creator.create(testRun));
    }

    @Override
    public String getDescription(TestRun testRun) throws Exception {
        return creator.getDescription(testRun);
    }

    public FixObjectCreator getCreator() {
        return creator;
    }

    public AbstractFixObjectConverter getFixObjectConverter() {
        return fixObjectConverter;
    }
}
