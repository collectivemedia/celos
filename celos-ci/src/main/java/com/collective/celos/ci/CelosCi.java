package com.collective.celos.ci;

import com.collective.celos.ci.config.deploy.CelosCiContext;
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

//        args = "--deployDir /home/akonopko/work/celos2/samples/wordcount2 --target sftp://107.170.177.172/home/akonopko/target.json --workflowName wordcount".split(" ");
//        args = ("--deployDir /home/akonopko/work/celos2/samples/wordcount/build/celos_deploy " +
//                "--target sftp://celos001/home/akonopko/target.json --workflowName wordcount " +
//                "--mode TEST --testDir /home/akonopko/work/celos2/samples/wordcount/src/test").split(" ");

//        ContextParser contextParser = new ContextParser();
//        contextParser.parse(args, new CelosCi());


        //        JsonElement root = new JsonParser().parse();
//        root.getAsJsonObject().entrySet();

        GenericDatumReader<Object> reader = new GenericDatumReader<Object>();

        ByteOutputStream stream1 = new ByteOutputStream();

        FileReader<Object> fileReader =  DataFileReader.openReader(new File("/home/akonopko/work/part-r-00000.avro"), reader);



        Schema schema;
        try {
            schema = fileReader.getSchema();
            System.out.println(schema);

//            DatumWriter<Object> writer = new GenericDatumWriter<>(schema);
//            JsonEncoder encoder = EncoderFactory.get().jsonEncoder(schema, stream1);
//
//            int i=0;
//            for (Object datum : fileReader) {
//                if (i++ == 10) {
//                    break;
//                };
//                writer.write(datum, encoder);
//            }
//            encoder.flush();
        } finally {
            fileReader.close();
        }




        GenericData.Record record = new GenericData.Record(schema);
        record.put("test", Integer.valueOf(10));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonEncoder jsonEncoder = EncoderFactory.get().jsonEncoder(schema, output);
        GenericDatumWriter<GenericData.Record> writer = new GenericDatumWriter<GenericData.Record>(schema);
        writer.write(record, jsonEncoder);
        jsonEncoder.flush();
        output.flush();


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
