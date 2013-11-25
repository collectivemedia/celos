package com.collective.celos;

public class TokenReplacer {

    public String replaceTimeTokens(String string, ScheduledTime t) {
        return string
            .replace("${year}", String.format("%04d", t.getYear()))
            .replace("${month}", String.format("%02d", t.getMonth()))
            .replace("${day}", String.format("%02d", t.getDay()))
            .replace("${hour}", String.format("%02d", t.getHour()));
    }
    
}
