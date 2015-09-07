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
