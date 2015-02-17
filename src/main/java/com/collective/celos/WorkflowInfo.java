package com.collective.celos;

import java.io.File;
import java.net.URI;
import java.util.List;

public class WorkflowInfo {

    public static class ContactsInfo {

        private final URI email;

        private final String name;

        public ContactsInfo(String name, URI email) {
            this.email = email;
            this.name = name;
        }

        public URI getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }
    }

    private final File workflowJSFile;
    private final URI URL;
    private final List<ContactsInfo> contacts;

    public WorkflowInfo(File workflowJSFile, URI url, List<ContactsInfo> contacts) {
        this.workflowJSFile = workflowJSFile;
        this.URL = url;
        this.contacts = contacts;
    }

    public File getWorkflowJSFile() {
        return workflowJSFile;
    }

    public URI getURL() {
        return URL;
    }

    public List<ContactsInfo> getContacts() {
        return contacts;
    }
}
