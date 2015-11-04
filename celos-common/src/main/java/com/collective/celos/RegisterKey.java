package com.collective.celos;

public class RegisterKey extends ValueObject {

    private final String keyString;

    public RegisterKey(String keyString) {
        this.keyString = Util.requireProperName(keyString);
    }

    public String toString() {
        return keyString;
    }

}
