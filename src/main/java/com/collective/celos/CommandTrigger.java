package com.collective.celos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class CommandTrigger implements Trigger {

    private static Logger LOGGER = Logger.getLogger(CommandTrigger.class);
    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
    private final List<String> rawCommandElements;

    public CommandTrigger(List<String> elements) throws Exception {
        rawCommandElements = Collections.unmodifiableList(elements);
    }
    
    @Override
    public boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime t) throws Exception {
        List<String> cookedCommandElements = new LinkedList<>();
        for (String rawElement : rawCommandElements) {
            cookedCommandElements.add(formatter.replaceTimeTokens(rawElement, t));
        }

        LOGGER.info("CommandTrigger: Prepared command: " + StringUtils.join(cookedCommandElements, " "));
        int result = new ProcessBuilder(cookedCommandElements).start().waitFor();
        LOGGER.info("CommandTrigger: exited with code " + result);
        return result == 0;
    }

    public List<String> getRawCommandElements() {
        return rawCommandElements;
    }

}
