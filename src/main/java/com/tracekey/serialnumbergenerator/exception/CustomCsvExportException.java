package com.tracekey.serialnumbergenerator.exception;

/**
 * Custom exception class for representing exceptions related to CSV exports in the application.
 * Extends RuntimeException to indicate a runtime exception that does not need to be explicitly caught.
 */
public class CustomCsvExportException extends RuntimeException {

    /**
     * Constructs a new CustomCsvExportException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public CustomCsvExportException(String message) {
        super(message);
    }

    /**
     * Constructs a new CustomCsvExportException with the specified detail message and cause.
     */
    public CustomCsvExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
