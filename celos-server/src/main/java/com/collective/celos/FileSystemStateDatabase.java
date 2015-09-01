package com.collective.celos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
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


    private static final String STATE_PATH  = "state";
    private static final String RERUN_PATH = "rerun";

    private final File dir;

    /**
     * Creates a new DB that stores data in the given directory, which must exist.
     */
    public FileSystemStateDatabase(File dir) throws IOException {
        this.dir = Util.requireNonNull(dir);
        if (!dir.exists()) {
            throw new IOException("Database directory " + dir + " doesn't exist.");
        }
        try {
            Files.createDirectory(dir.toPath().resolve(RERUN_PATH));
        } catch (FileAlreadyExistsException ignored) {}
        try {
            Files.createDirectory(dir.toPath().resolve(STATE_PATH));
        } catch (FileAlreadyExistsException ignored) {}
    }

    @Override
    public SlotState getSlotState(SlotID id) throws Exception {
        File file = getSlotFile(id).toFile();
        if (!file.exists()) {
            return null;
        } else {
            // race
            JsonNode node = readJsonFromPath(file.toPath());
            return SlotState.fromJSONNode(id, node);
        }
    }

    @Override
    public void putSlotState(SlotState state) throws Exception {
        final Path file = getSlotFile(state.getSlotID());
        LOGGER.warn(file);
        writeJsonableToPath(state.toJSONNode(), file);
    }

    /** Returns the directory containing all data for the slot's workflow. */
    private Path getWorkflowDir(WorkflowID wfId) {
        return dir.toPath().resolve(STATE_PATH).resolve(wfId.toString());
    }

    private Path getRerunWorkflowPath(WorkflowID wfId) {
        return dir.toPath().resolve(RERUN_PATH).resolve(wfId.toString());
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
        writeJsonableToPath(rerunState.toJsonNode(), slotFile);
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
    public List<SlotID> getRerunSlotIDs(WorkflowID wfId, ScheduledTime current) throws Exception {
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
                assert file != null;
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


    private Path getSlotFile(SlotID slotID) {
        return getDayDir(slotID).resolve(getSlotFileName(slotID));
    }

    /** Returns the directory containing a day's data inside the workflow dir. */
    private Path getDayDir(SlotID slotID) {
        Path workflowDir = getWorkflowDir(slotID.getWorkflowID());
        Path dayDir = workflowDir.resolve(FORMATTER.formatDatestamp(slotID.getScheduledTime()));
        return dayDir;
    }

    /** Returns the directory containing all data for the slot's workflow. */
    private File getWorkflowDir(SlotID slotID) {
        return dir.toPath().resolve("state").resolve(slotID.getWorkflowID().toString()).toFile();
    }
    
    private String getSlotFileName(SlotID slotID) {
        return FORMATTER.formatTimestamp(slotID.getScheduledTime());
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Charset CHARSET = Charset.forName("UTF-8");

    public static void writeJsonableToPath(JsonNode obj, Path path) throws Exception {
        assert path != null;
        Files.createDirectories(path.getParent());
        final String json = MAPPER.writeValueAsString(obj);
        Files.write(path, json.getBytes(CHARSET));
    }

    public static JsonNode readJsonFromPath(Path path) throws Exception {
        assert path != null;
        assert Files.isRegularFile(path);
        String jsonData = new String(Files.readAllBytes(path), CHARSET);
        return MAPPER.readTree(jsonData);
    }

}
