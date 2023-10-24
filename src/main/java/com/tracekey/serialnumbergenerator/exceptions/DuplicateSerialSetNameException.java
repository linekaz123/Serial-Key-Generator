package com.tracekey.serialnumbergenerator.exceptions;

/**
 * Exception thrown when attempting to create a serial set with a duplicate name.
 */
public class DuplicateSerialSetNameException extends RuntimeException {
    public DuplicateSerialSetNameException(String message) {
        super(message);
    }
}