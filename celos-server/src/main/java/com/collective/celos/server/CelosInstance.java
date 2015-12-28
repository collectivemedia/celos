package com.collective.celos.server;

import java.util.Comparator;

public class CelosInstance {

    private final String nodePath;

    public CelosInstance(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getNodePath() {
        return nodePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CelosInstance that = (CelosInstance) o;

        if (nodePath != null ? !nodePath.equals(that.nodePath) : that.nodePath != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return nodePath != null ? nodePath.hashCode() : 0;
    }

    public static Comparator<CelosInstance> ID_COMPARATOR = (o1, o2) -> o1.getNodePath().compareTo(o2.getNodePath());

}
