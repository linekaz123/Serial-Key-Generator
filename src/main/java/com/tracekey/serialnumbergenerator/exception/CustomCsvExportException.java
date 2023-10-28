package com.tracekey.serialnumbergenerator.exception;

public class CustomCsvExportException extends RuntimeException {

    public CustomCsvExportException(String message) {
        super(message);
    }

    public CustomCsvExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
