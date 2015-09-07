package com.collective.celos;

/**
 * Exception from external service.
 */
@SuppressWarnings("serial")
public class ExternalServiceException extends Exception {

    public ExternalServiceException(Throwable t) {
        super(t);
    }
    
}
