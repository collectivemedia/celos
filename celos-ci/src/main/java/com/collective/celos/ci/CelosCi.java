package com.collective.celos.ci;

import com.collective.celos.ci.config.ContextParser;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.config.testing.TestContext;
import com.collective.celos.ci.deploy.HdfsDeployer;
import com.collective.celos.ci.deploy.WorkflowFileDeployer;
import com.collective.celos.ci.testing.CelosCiIntegrationTestRunner;
import com.google.gson.*;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.FileReader;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CelosCi {

    public static void main(String... args) throws Exception {

        ContextParser contextParser = new ContextParser(new CelosCiTargetParser());
        contextParser.parse(args, new CelosCi());
    }



    public void onDeployMode(CelosCiContext ciContext) throws Exception {
        WorkflowFileDeployer wfDeployer = new WorkflowFileDeployer(ciContext);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(ciContext);
        wfDeployer.deploy();
        hdfsDeployer.deploy();
    }

    public void onUndeployMode(CelosCiContext ciContext) throws Exception {
        WorkflowFileDeployer wfDeployer = new WorkflowFileDeployer(ciContext);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(ciContext);
        wfDeployer.undeploy();
        hdfsDeployer.undeploy();
    }

    public void onTestMode(CelosCiContext ciContext, TestContext testContext) throws Exception {
        new CelosCiIntegrationTestRunner(ciContext, testContext).runTests();
    }


}
