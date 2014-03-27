package com.collective.celos;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.collective.celos.api.Util;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This external service launches potentially long-running UNIX processes and tracks their status.
 * 
 * It maintains a job database by default at /var/lib/celos/jobs containing information about each job.
 * 
 * It does this by means of a small wrapper script (src/main/bash/celos-wrapper), which forks the process.
 * 
 * The process is started as an orphan process (not a child of Celos), so it will keep on running even
 * if Celos is restarted for maintenance or deployment.
 * 
 * Each started process is assigned a UUID which serves as the external ID in the Celos API.
 * 
 * The service creates a job directory at:
 * 
 * /var/lib/celos/jobs/$workflow-name/$YYYY-$MM-$DD/$UUID
 * 
 * The job directory contains the following files:
 * 
 * - cmd: the executed command line
 * 
 * - pid: the process identifier of the launched process
 * 
 * - status: if the process has finished, contains its exit
 * 
 * - out: the stdout of the process
 * 
 * - err: the stderr of the process
 */
public class CommandExternalService implements ExternalService {

    public static final String COMMAND_PROP = "celos.commandExternalService.command";
    public static final String WRAPPER_COMMAND_PROP = "celos.commandExternalService.wrapperCommand";
    public static final String DATABASE_DIR_PROP = "celos.commandExternalService.databaseDir";
    
    public static final String PID_FILE_NAME = "pid";
    public static final String EXIT_CODE_FILE_NAME = "status";
    public static final String STDOUT_FILE_NAME = "out";
    public static final String STDERR_FILE_NAME = "err";
    
    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
    private final String rawCommand;
    private final String wrapperCommand;
    private final File databaseDir;
    
    private final Logger LOGGER = Logger.getLogger(CommandExternalService.class);
    
    public CommandExternalService(ObjectNode properties) {
        this.rawCommand = Util.getStringProperty(properties, COMMAND_PROP);
        this.wrapperCommand = Util.getStringProperty(properties, WRAPPER_COMMAND_PROP);
        this.databaseDir = new File(Util.getStringProperty(properties, DATABASE_DIR_PROP));
    }
    
    @Override
    public String submit(SlotID id) throws ExternalServiceException {
        return UUID.randomUUID().toString();
    }

    @Override
    public void start(SlotID id, String externalID) throws ExternalServiceException {
        String command = formatter.replaceTimeTokens(getRawCommand(), id.getScheduledTime());
        File jobDir = getJobDir(id, externalID);
        if (jobDir.exists()) {
            throw new IllegalStateException("Job directory " + jobDir + " already exists.");
        }
        if (!jobDir.mkdirs()) {
            throw new RuntimeException("Cannot create job directory " + jobDir);
        }
        ProcessBuilder pb = new ProcessBuilder(wrapperCommand, command, jobDir.toString());
        try {
            int wrapperExitCode = pb.start().waitFor();
            if (wrapperExitCode != 0) {
                throw new RuntimeException("Wrapper exited with status code " + wrapperExitCode);
            }
        } catch (Exception e) {
            throw new ExternalServiceException(e);
        }
        LOGGER.info("Started command: " + command + " with job dir: " + jobDir + " for: " + id);
    }

    @Override
    public ExternalStatus getStatus(SlotID id, String externalID) throws ExternalServiceException {
        File jobDir = getJobDir(id, externalID);
        if (!jobDir.exists()) {
            throw new RuntimeException("Job directory " + jobDir + " doesn't exist");
        }
        File pidFile = getPidFile(jobDir);
        if (!pidFile.exists()) {
            throw new RuntimeException("Pid file " + pidFile + " doesn't exist");
        }
        File exitCodeFile = getExitCodeFile(jobDir);
        if (!exitCodeFile.exists()) {
            return RUNNING;
        } else {
            int exitCode = getExitCode(exitCodeFile);
            if (exitCode == 0) {
                return SUCCESS;
            } else {
                return FAILURE;
            }
        }
    }
    
    private int getExitCode(File exitCodeFile) {
        try {
            String exitCodeString = IOUtils.toString(new FileInputStream(exitCodeFile));
            return Integer.parseInt(exitCodeString.trim());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File getJobDir(SlotID id, String externalID) {
        return new File(getDayDir(id), externalID);
    }

    private File getDayDir(SlotID slotID) {
        File workflowDir = getWorkflowDir(slotID);
        File dayDir = new File(workflowDir, formatter.formatDatestamp(slotID.getScheduledTime()));
        return dayDir;
    }

    private File getWorkflowDir(SlotID slotID) {
        return new File(databaseDir, slotID.getWorkflowID().toString());
    }

    private File getPidFile(File jobDir) {
        return new File(jobDir, PID_FILE_NAME);
    }
    
    private File getExitCodeFile(File jobDir) {
        return new File(jobDir, EXIT_CODE_FILE_NAME);
    }
    
    public String getRawCommand() {
        return rawCommand;
    }

    private static class CommandExternalStatus implements ExternalStatus {

        private boolean running;
        private boolean success;
        
        public CommandExternalStatus(boolean running, boolean success) {
            this.running = running;
            this.success = success;
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }
        
    }
    
    public static final ExternalStatus RUNNING = new CommandExternalStatus(true, false);
    public static final ExternalStatus SUCCESS = new CommandExternalStatus(false, true);
    public static final ExternalStatus FAILURE = new CommandExternalStatus(false, false);
    
}
