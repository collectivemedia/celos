package com.collective.celos;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FileSystemRerunDatabase implements RerunDatabase {

    private final Path rerunDir;

    private static Logger LOGGER = Logger.getLogger(FileSystemRerunDatabase.class);

    private static final ScheduledTimeFormatter FORMATTER = new ScheduledTimeFormatter();

    /**
     * Creates a new DB that stores data in the given directory, which must exist.
     */
    public FileSystemRerunDatabase(Path rerunDir) throws IOException {
        assert rerunDir != null;
        assert Files.isDirectory(rerunDir); // Database rerun directory must exists.
        this.rerunDir = rerunDir;
    }

    // Note a slot ID for rerun processing at the (current) wallclock time.
    @Override
    public void addSlotID(SlotID id, ScheduledTime current) throws Exception {
        final RerunState rerunState = RerunState.fromTime(id.getWorkflowID(), id.getScheduledTime(), current);
        final Path slotFile = getSlotPath(id);
        Util.writeJsonableToPath(rerunState.toJsonNode(), slotFile);
    }

    // Get all slot IDs of the workflow noted for rerun processing. Also garbage collect any too-old rerun notes.
    @Override
    public List<SlotID> getSlotIDs(WorkflowID wfId, ScheduledTime current) throws Exception {
        List<SlotID> res = Collections.emptyList();
        File wfPath = getWorkflowPath(wfId).toFile();
        File[] daysDir = wfPath.listFiles();
        assert daysDir != null;
        for (File slotsDir : daysDir) {
            final Path slotsDirPath = slotsDir.toPath();
            assert Files.isDirectory(slotsDirPath);
            File[] listSlotFiles = slotsDir.listFiles();
            assert listSlotFiles != null;
            for (File file : listSlotFiles) {
                // if rerunTime > GC_TIME remove this node
                JsonNode node = Util.readJsonFromPath(file.toPath());
                RerunState st = RerunState.fromJsonNode(node);
                if (st.isExpired(current)) {
                    LOGGER.warn("rerun file " + file.getAbsolutePath() + " expired");
                    Files.deleteIfExists(file.toPath());
                }
                SlotID slotId = new SlotID(st.getWorkflowId(), st.getSlotTime());
                res.add(slotId);
            }
            if (Util.isDirectoryEmpty(slotsDirPath)) {
                Files.deleteIfExists(slotsDirPath);
            }
        }
        return res;
    }

    private Path getWorkflowPath(WorkflowID wfId) {
        return rerunDir.resolve(wfId.toString());
    }

    private Path getSlotPath(SlotID slotId) {
        final String dayRepr = FORMATTER.formatDatestamp(slotId.getScheduledTime());
        final String timeRepr = FORMATTER.formatTimestamp(slotId.getScheduledTime());
        final Path wfPath = getWorkflowPath(slotId.getWorkflowID());
        return wfPath.resolve(dayRepr).resolve(timeRepr);
    }

}
