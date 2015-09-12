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

import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Metadata such as URL and authors of a workflow.
 */
public class WorkflowInfo {

    public static class ContactsInfo {

        private final URI email;

        private final String name;

        private ContactsInfo() {
            this.email = null;
            this.name = null;
        }

        public ContactsInfo(String name, String email) {
            this.email = email == null ? null : URI.create(email);
            this.name = name;
        }

        public URI getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }
    }

    private final URL url;
    private final List<ContactsInfo> contacts;

    private WorkflowInfo() {
        this.url = null;
        this.contacts = null;
    }

    public WorkflowInfo(URL url, List<ContactsInfo> contacts) {
        this.url = url;
        this.contacts = contacts;
    }

    public URL getUrl() {
        return url;
    }

    public List<ContactsInfo> getContacts() {
        return contacts;
    }

}
