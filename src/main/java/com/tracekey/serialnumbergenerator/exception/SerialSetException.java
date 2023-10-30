package com.tracekey.serialnumbergenerator.exception;

/**
 * Custom exception class for representing exceptions related to the SerialSet functionality.
 * Extends RuntimeException to indicate a runtime exception that does not need to be explicitly caught.
 */
public class SerialSetException extends RuntimeException {

    /**
     * Constructs a new SerialSetException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public SerialSetException(String message) {
        super(message);
    }

}
