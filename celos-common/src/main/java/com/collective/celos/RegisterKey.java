package com.collective.celos;

public class RegisterKey extends ValueObject implements Comparable<RegisterKey> {

    private final String keyString;

    public RegisterKey(String keyString) {
        this.keyString = Util.requireProperName(keyString);
    }

    public String toString() {
        return keyString;
    }
    
    @Override
    public int compareTo(RegisterKey o) {
        Util.requireNonNull(o);
        return keyString.compareTo(o.keyString);
    }

}
