package com.collective.celos;

public class BucketID extends ValueObject {

    private final String id;

    public BucketID(String id) {
        this.id = Util.requireProperName(id);
    }

    public String toString() {
        return id;
    }

}
