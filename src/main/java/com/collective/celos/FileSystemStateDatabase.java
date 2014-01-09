package com.collective.celos;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Brutally simple persistent implementation of StateDatabase
 * that stores slot states as JSON files in the file system.
 * 
 * The database has a top-level directory.  Inside it, there
 * is a subdirectory for each workflow.  Inside each workflow directory
 * is one JSON file for each scheduled time.
 * 
 * /
 *   workflow-1/
 *     2013-12-02T16:00:00.000Z
 *     2013-12-02T17:00:00.000Z
 *     ...
 *   workflow-2/
 *     2013-12-02T16:00:00.000Z
 *     2013-12-02T17:00:00.000Z
 *     ...
 *   ...
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

    private final ObjectMapper mapper = new ObjectMapper();
    private final File dir;
    
    /**
     * Creates a new DB that stores data in the given directory, which must exist.
     */
    public FileSystemStateDatabase(File dir) throws IOException {
        this.dir = Util.requireNonNull(dir); 
        if (!dir.exists()) {
            throw new IOException("Database directory " + dir + " doesn't exist.");
        }
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
        return new File(getWorkflowDir(slotID), getSlotFileName(slotID));
    }

    private File getWorkflowDir(SlotID slotID) {
        return new File(dir, slotID.getWorkflowID().toString());
    }
    
    private String getSlotFileName(SlotID slotID) {
        return slotID.getScheduledTime().getDateTime().toString();
    }

}
