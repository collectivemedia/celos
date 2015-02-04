package com.collective.celos;

import java.util.UUID;

/**
 * Created by akonopko on 04.02.15.
 */
public class DatabaseName extends ValueObject {

    private final static String AUGMENTED_DB_NAME = "celosci_%s_%s";

    private final String databaseName;

    public DatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getMockedName(UUID testUuid) {
        return String.format(AUGMENTED_DB_NAME, databaseName, testUuid.toString().replace("-", "_"));
    }

}
