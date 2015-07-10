package com.collective.celos;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Brutally simple persistent implementation of StateDatabase
 * that stores slot states as JSON files in the file system.
 * 
 * The database has a top-level directory.  Inside it, there
 * is a subdirectory for each workflow.  Inside each workflow directory
 * is one directory per day.  Inside each day directory is one JSON file
 * per workflow run.
 * 
 * /
 *   workflow-1/
 *     2013-12-02/
 *       16:00:00.000Z
 *       17:00:00.000Z
 *     ...
 *   workflow-2/
 *     2013-12-02/
 *       16:00:00.000Z
 *       17:00:00.000Z
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

    private static final ScheduledTimeFormatter FORMATTER = new ScheduledTimeFormatter();

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
            JsonNode node = Util.readJsonFromPath(file.toPath());
            return SlotState.fromJSONNode(id, node);
        }
    }

    @Override
    public void putSlotState(SlotState state) throws Exception {
        final Path file = getSlotFile(state.getSlotID()).toPath();
        Util.writeJsonableToPath(state.toJSONNode(), file);
    }

    private File getSlotFile(SlotID slotID) {
        return new File(getDayDir(slotID), getSlotFileName(slotID));
    }

    /** Returns the directory containing a day's data inside the workflow dir. */
    private File getDayDir(SlotID slotID) {
        File workflowDir = getWorkflowDir(slotID);
        File dayDir = new File(workflowDir, FORMATTER.formatDatestamp(slotID.getScheduledTime()));
        return dayDir;
    }

    /** Returns the directory containing all data for the slot's workflow. */
    private File getWorkflowDir(SlotID slotID) {
        return new File(dir, slotID.getWorkflowID().toString());
    }
    
    private String getSlotFileName(SlotID slotID) {
        return FORMATTER.formatTimestamp(slotID.getScheduledTime());
    }

}
