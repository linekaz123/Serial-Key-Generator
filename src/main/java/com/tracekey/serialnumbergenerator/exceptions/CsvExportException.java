package com.tracekey.serialnumbergenerator.exceptions;

/**
 * Exception thrown when an error occurs during the CSV export process.
 */
public class CsvExportException extends RuntimeException {
    public CsvExportException(String message, Throwable cause) {
        super(message, cause);
    }
}