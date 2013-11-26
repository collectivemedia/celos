package com.collective.celos;

public class Util {

    public static <T> T requireNonNull(T object) {
        if (object == null) throw new NullPointerException();
        else return object;
    }

    public static void logException(Exception e) {
        e.printStackTrace(System.err);
    }
    
}
