package com.collective.celos.ci.fixtures;

/**
 * Created by akonopko on 9/18/14.
 */
public class CelosResultsCompareException extends Exception {

    public CelosResultsCompareException() {
    }

    public CelosResultsCompareException(String message) {
        super(message);
    }

    public CelosResultsCompareException(String message, Throwable cause) {
        super(message, cause);
    }

    public CelosResultsCompareException(Throwable cause) {
        super(cause);
    }

    public CelosResultsCompareException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
