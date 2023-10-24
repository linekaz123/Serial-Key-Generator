package com.tracekey.serialnumbergenerator.exceptions;

/**
 * Exception thrown when invalid criteria are encountered during CSV export.
 */
public class CsvExportCriteriaException extends RuntimeException {
    public CsvExportCriteriaException(String message) {
        super(message);
    }
}