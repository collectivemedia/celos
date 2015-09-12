/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import java.util.UUID;

/**
 * Utility class for Celos CI that wraps a database name.
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
