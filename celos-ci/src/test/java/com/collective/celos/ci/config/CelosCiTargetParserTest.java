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
package com.collective.celos.ci.config;

import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;

/**
 * Created by akonopko on 9/29/14.
 */
public class CelosCiTargetParserTest {

    @Test
    public void testParseTargetFile() throws Exception {
        CelosCiTargetParser parser = new CelosCiTargetParser("uname");
        File targetFile = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/target.json").toURI());

        CelosCiTarget target = parser.parse(targetFile.toURI());
        Assert.assertEquals(target.getWorkflowsDirUri(), URI.create("celoswfdir"));
        Assert.assertEquals(target.getDefaultsDirUri(), URI.create("defdir"));
        Assert.assertEquals(target.getPathToCoreSite(), URI.create("hadoopcoreurl"));
        Assert.assertEquals(target.getPathToHdfsSite(), URI.create("hadoophdfsurl"));

    }


    @Test (expected = IllegalStateException.class)
    public void testParseTargetFileNoWfDir() throws Exception {
        CelosCiTargetParser parser = new CelosCiTargetParser("uname");
        File targetFile = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/target-no-wf-uri.json").toURI());

        parser.parse(targetFile.toURI());
    }

    @Test (expected = IllegalStateException.class)
    public void testParseTargetFileNoDeffile() throws Exception {
        CelosCiTargetParser parser = new CelosCiTargetParser("uname");
        File targetFile = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/target-no-deffile.json").toURI());

        parser.parse(targetFile.toURI());
    }


}
