package com.collective.celos;

public class BucketID extends ValueObject implements Comparable<BucketID> {

    private final String id;

    public BucketID(String id) {
        this.id = Util.requireProperName(id);
    }

    public String toString() {
        return id;
    }

    @Override
    public int compareTo(BucketID o) {
        Util.requireNonNull(o);
        return id.compareTo(o.id);
    }

}
