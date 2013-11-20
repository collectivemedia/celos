package com.collective.celos;

import java.util.Properties;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("main():");
        System.out.flush();
        
        ExternalService service = new OozieExternalService();

        if (args.length == 0) {
            System.out.println("Usage: Main [ -run | -info jobId ]");
            System.exit(0);
        }
        
        if (args[0].equals("-run")) {
            String jobId = service.run(new Properties());
            System.out.println("jobId=" + jobId);
        } else if (args[0].equals("-info")) {
            ExternalStatus status = service.getStatus(args[1]);
            System.out.println("status=" + status);
        }
        
    }
}
