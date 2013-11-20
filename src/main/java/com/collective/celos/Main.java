package com.collective.celos;

import java.util.Properties;

public class Main {

    /*
     * NOTE: this is just so that I can do manual testing.
     */
    public static void main(String[] args) throws Exception {
        
        ExternalService service = new OozieExternalService();

        if (args.length == 0) {
            System.out.println("Usage: Main [ -run | -info jobId ]");
            System.exit(0);
        }
        
        if (args[0].equals("-run")) {
            Properties props = new Properties();
            props.setProperty("user.name", "iwilliams");
            props.setProperty("oozie.wf.application.path", "/user/iwilliams/celos-workflow.xml");
            props.setProperty("jobTracker", "nn01.ny7.collective-media.net:8032");
            props.setProperty("nameNode", "hdfs://cluster-ny7");
            String jobId = service.run(props);
            System.out.println("jobId=" + jobId);
        } else if (args[0].equals("-info")) {
            ExternalStatus status = service.getStatus(args[1]);
            System.out.println("status=" + status.getStatus());
        }
        
    }
}

