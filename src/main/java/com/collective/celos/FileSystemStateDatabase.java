package com.collective.celos;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
    private static final Logger LOGGER = Logger.getLogger(FileSystemStateDatabase.class);

    private final File dir;

    private final Path rerunDir;

    /**
     * Creates a new DB that stores data in the given directory, which must exist.
     */
    public FileSystemStateDatabase(File dir, File rerunDir) throws IOException {
        assert dir != null;
        assert Files.isDirectory(dir.toPath()); // Database state directory must exists.
        this.dir = dir;
        assert rerunDir != null;
        assert Files.isDirectory(rerunDir.toPath()); // Database rerun directory must exists.
        this.rerunDir = rerunDir.toPath();
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


    private Path getRerunWorkflowPath(WorkflowID wfId) {
        return rerunDir.resolve(wfId.toString());
    }


    private Path getRerunSlotPath(SlotID slotId) {
        final String dayRepr = FORMATTER.formatDatestamp(slotId.getScheduledTime());
        final String timeRepr = FORMATTER.formatTimestamp(slotId.getScheduledTime());
        final Path wfPath = getRerunWorkflowPath(slotId.getWorkflowID());
        return wfPath.resolve(dayRepr).resolve(timeRepr);
    }


    private void addRerunSlotID(SlotID id, ScheduledTime current) throws Exception {
        final RerunState rerunState = RerunState.fromTime(id.getWorkflowID(), id.getScheduledTime(), current);
        final Path slotFile = getRerunSlotPath(id);
        Util.writeJsonableToPath(rerunState.toJsonNode(), slotFile);
    }


    @Override
    public boolean updateSlotToRerun(SlotID slot, ScheduledTime current) throws Exception {
        SlotState state = getSlotState(slot);
        if (state == null) {
            throw new IllegalStateException();
        }
        final SlotState slotState = state.transitionToRerun();
        putSlotState(slotState);
        addRerunSlotID(slotState.getSlotID(), current);
        return true;
    }

    @Override
    public List<SlotID> getSlotIDs(WorkflowID wfId, ScheduledTime current) throws Exception {
        final List<SlotID> res = new ArrayList<>();
        final Path wfPath = getRerunWorkflowPath(wfId);
        if (!Files.isDirectory(wfPath)) {
            return res;
        }
        // else
        final File[] daysDir = wfPath.toFile().listFiles();
        assert daysDir != null;
        for (File slotsDir : daysDir) {
            final Path slotsDirPath = slotsDir.toPath();
            assert Files.isDirectory(slotsDirPath);
            File[] listSlotFiles = slotsDir.listFiles();
            assert listSlotFiles != null;
            for (File file : listSlotFiles) {
                final JsonNode node = Util.readJsonFromPath(file.toPath());
                final RerunState st = RerunState.fromJsonNode(node);
                if (st.isExpired(current)) {
                    LOGGER.warn("rerun file " + file.getAbsolutePath() + " expired");
                    Files.deleteIfExists(file.toPath());
                } else {
                    final SlotID slotId = new SlotID(st.getWorkflowId(), st.getSlotTime());
                    res.add(slotId);
                }
            }
            if (Util.isDirectoryEmpty(slotsDirPath)) {
                Files.deleteIfExists(slotsDirPath);
            }
        }
        return res;
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
