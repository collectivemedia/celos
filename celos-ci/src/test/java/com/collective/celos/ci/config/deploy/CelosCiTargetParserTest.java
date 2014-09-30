package com.collective.celos.ci.config.deploy;

import com.collective.celos.ci.deploy.JScpWorker;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by akonopko on 9/29/14.
 */
public class CelosCiTargetParserTest {

    @Test
    public void testParseTargetFile() throws Exception {
        CelosCiTargetParser parser = new CelosCiTargetParser("uname");
        File targetFile = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/target.json").toURI());

        CelosCiTarget target = parser.parse(targetFile.getAbsolutePath());
        Assert.assertEquals(target.getCelosWorkflowsDirUri(), "celoswfdir");
        Assert.assertEquals(target.getDefaultsFile(), "deffile");
        Assert.assertEquals(target.getPathToCoreSite(), "hadoopcoreurl");
        Assert.assertEquals(target.getPathToHdfsSite(), "hadoophdfsurl");
        Assert.assertEquals(target.getScpSecuritySettings(), "secsettings");

    }

    @Test
    public void testParseTargetFileDefaultSecuritySettings() throws Exception {
        CelosCiTargetParser parser = new CelosCiTargetParser("uname");
        File targetFile = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/target-no-sec.json").toURI());

        CelosCiTarget target = parser.parse(targetFile.getAbsolutePath());
        Assert.assertEquals(target.getCelosWorkflowsDirUri(), "celoswfdir");
        Assert.assertEquals(target.getDefaultsFile(), "deffile");
        Assert.assertEquals(target.getPathToCoreSite(), "hadoopcoreurl");
        Assert.assertEquals(target.getPathToHdfsSite(), "hadoophdfsurl");
        Assert.assertEquals(target.getScpSecuritySettings(), JScpWorker.DEFAULT_SECURITY_SETTINGS);

    }

}
