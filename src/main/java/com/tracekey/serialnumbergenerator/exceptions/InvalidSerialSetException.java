package com.tracekey.serialnumbergenerator.exceptions;

/**
 * Exception thrown when an invalid serial set is encountered.
 */
public class InvalidSerialSetException extends RuntimeException {
    public InvalidSerialSetException(String message) {
        super(message);
    }
}