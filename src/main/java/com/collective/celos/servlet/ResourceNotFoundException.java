package com.collective.celos.servlet;

/**
 * Created by akonopko on 05.02.15.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }


}
