package com.collective.celos;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private static final String STATUS_PROP = "status";
    private static final String EXTERNAL_ID_PROP = "externalID";
    
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
            return slotStateFromJSON(id, json);            
        }
    }

    @Override
    public void putSlotState(SlotState state) throws Exception {
        String json = slotStateToJSON(state);
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

    SlotState slotStateFromJSON(SlotID id, String json) throws Exception {
        TypeReference<HashMap<String,String>> typeRef =
                new TypeReference<HashMap<String,String>>() {}; 
        Map<String, String> properties = mapper.readValue(json, typeRef);
        SlotState.Status status = SlotState.Status.valueOf(properties.get(STATUS_PROP));
        SlotState state = new SlotState(id, status);
        String externalID = properties.get(EXTERNAL_ID_PROP);
        if (externalID != null) {
            state.setExternalID(externalID);
        }
        return state;
    }

    String slotStateToJSON(SlotState state) throws Exception {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(STATUS_PROP, state.getStatus().toString());
        if (state.getExternalID() != null) {
            properties.put(EXTERNAL_ID_PROP, state.getExternalID());
        }
        return mapper.writeValueAsString(properties);
    }

}
