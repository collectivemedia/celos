package com.collective.celos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Brutally simple persistent implementation of StateDatabase that stores slot
 * states as JSON files in the file system.
 * 
 * The database has a top-level directory, with one sub directory, called state.
 * 
 * Inside the state directory, there is a subdirectory for each workflow. Inside
 * each workflow directory is one directory per day. Inside each day directory
 * is one JSON file per workflow run.
 * 
 * / workflow-1/ 2013-12-02/ 16:00:00.000Z 17:00:00.000Z ... workflow-2/
 * 2013-12-02/ 16:00:00.000Z 17:00:00.000Z ... ...
 * 
 * A JSON file looks like this:
 * 
 * {"status":"WAITING"}
 * 
 * or
 * 
 * {"status":"RUNNING","externalID":"23873218-13202130978213-W"}
 */
public class FileSystemStateDatabase implements StateDatabase {

    private static final String CHARSET = "UTF-8";
    private static final String STATE_DIR_NAME = "state";

    private final ObjectMapper mapper = new ObjectMapper();
    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
    private final File toplevelDir;
    private final File stateDir;
    
    /**
     * Creates a new DB that stores data in the given directory, which must exist.
     */
    public FileSystemStateDatabase(File dir) throws IOException {
        this.toplevelDir = Util.requireNonNull(dir);
        if (!toplevelDir.exists()) {
            throw new IOException("Database directory " + toplevelDir + " doesn't exist.");
        }
        stateDir = new File(dir, STATE_DIR_NAME);
    }

    @Override
    public SlotState getSlotState(SlotID id) throws Exception {
        File file = getSlotFile(id);
        if (!file.exists()) {
            return null;
        } else {
            String json = FileUtils.readFileToString(file, CHARSET);
            return SlotState.fromJSONNode(id, (ObjectNode) mapper.readTree(json));
        }
    }

    @Override
    public void putSlotState(SlotState state) throws Exception {
        String json = mapper.writeValueAsString(state.toJSONNode());
        File file = getSlotFile(state.getSlotID());
        FileUtils.forceMkdir(file.getParentFile());
        FileUtils.write(file, json, CHARSET);
    }

    private File getSlotFile(SlotID slotID) {
        return new File(getDayDir(slotID), getSlotFileName(slotID));
    }

    /** Returns the directory containing a day's data inside the workflow dir. */
    private File getDayDir(SlotID slotID) {
        File workflowDir = getWorkflowDir(slotID);
        File dayDir = new File(workflowDir, formatter.formatDatestamp(slotID.getScheduledTime()));
        return dayDir;
    }

    /** Returns the directory containing all data for the slot's workflow. */
    private File getWorkflowDir(SlotID slotID) {
        return new File(stateDir, slotID.getWorkflowID().toString());
    }
    
    private String getSlotFileName(SlotID slotID) {
        return formatter.formatTimestamp(slotID.getScheduledTime());
    }

}
