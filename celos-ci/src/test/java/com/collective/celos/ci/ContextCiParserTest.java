package com.collective.celos.ci;

import com.collective.celos.ci.config.ContextParser;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.config.testing.TestContext;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by akonopko on 9/29/14.
 */
public class ContextCiParserTest {

    @Test
    public void testCreateConfigOnTestMode() throws Exception {

        String[] args = "--deployDir deployDir --target testTarget --workflowName wordcount --mode TEST --testDir testDir".split(" ");

        CelosCiTargetParser targetParser = mock(CelosCiTargetParser.class);
        CelosCiTarget target = mock(CelosCiTarget.class);
        Mockito.doReturn(target).when(targetParser).parse("testTarget");

        ContextParser contextParser = new ContextParser(targetParser);

        CelosCi celosCi = Mockito.spy(new CelosCi());

        Mockito.doNothing().when(celosCi).onTestMode(any(CelosCiContext.class), any(TestContext.class));

        String coreSite = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/core-site.xml").toURI().toString();
        String hdfsSite = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/hdfs-site.xml").toURI().toString();

        Mockito.doReturn(coreSite).when(target).getPathToCoreSite();
        Mockito.doReturn(hdfsSite).when(target).getPathToHdfsSite();

        contextParser.parse(args, "", celosCi);

        verify(celosCi, times(1)).onTestMode(any(CelosCiContext.class), any(TestContext.class));

    }
}
