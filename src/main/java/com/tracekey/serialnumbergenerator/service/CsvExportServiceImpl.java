package com.tracekey.serialnumbergenerator.service;

import com.tracekey.serialnumbergenerator.entity.SerialNumber;
import com.tracekey.serialnumbergenerator.entity.SerialSet;
import com.tracekey.serialnumbergenerator.exception.CustomCsvExportException;
import com.tracekey.serialnumbergenerator.repository.SerialSetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of the CsvExportService interface for exporting serial numbers to CSV.
 */
@Service
@Slf4j
public class CsvExportServiceImpl implements ICsvExportService {

    // Configuration values loaded from properties
    @Value("${csv.export.directory}")
    private String exportDirectory;

    // Error messages
    private static final String EXPORT_ERROR_MESSAGE = "Error exporting serial numbers to CSV";
    private static final String NOT_FOUND_ERROR_MESSAGE = "Serial set not found for export";
    private static final String NO_SERIAL_NUMBERS_ERROR_MESSAGE = "No serial numbers created for this serial set";
    private static final String INCOMPLETE_GENERATION_ERROR_MESSAGE = "Serial set numbers generation is incomplete";

    // Repository for serial sets
    private final SerialSetRepository serialSetRepository;

    /**
     * Constructor for injecting the serial set repository.
     *
     * @param serialSetRepository Repository for serial sets
     */
    public CsvExportServiceImpl(SerialSetRepository serialSetRepository) {
        this.serialSetRepository = serialSetRepository;
    }

    /**
     * Exports serial numbers to CSV for a given serial set name.
     *
     * @param serialSetName The name of the serial set to export
     * @return True if export is successful, false otherwise
     */
    @Override
    public boolean exportSerialNumbersToCSV(String serialSetName) {
        log.info("Exporting serial numbers to CSV for serial set: {}", serialSetName);
        SerialSet serialSet = getValidSerialSet(serialSetName);

        try {
            exportToCsv(serialSet);
            log.info("Serial numbers exported to CSV successfully for serial set: {}", serialSetName);
            return true;
        } catch (IOException e) {
            log.error("Error exporting serial numbers to CSV for serial set: {}", serialSetName, e);
            throw new CustomCsvExportException(EXPORT_ERROR_MESSAGE, e);
        }
    }

    /**
     * Retrieves a valid serial set based on the provided serial set name.
     *
     * @param serialSetName The name of the serial set to retrieve
     * @return The valid serial set
     */
    private SerialSet getValidSerialSet(String serialSetName) {
        log.info("Fetching and validating serial set for export: {}", serialSetName);
        SerialSet serialSet = serialSetRepository.findByName(serialSetName);

        if (serialSet == null) {
            log.error("Serial set validation failed. Not found for export: {}", serialSetName);
            throw new CustomCsvExportException(NOT_FOUND_ERROR_MESSAGE);
        }

        if (serialSet.getSerialNumbers() == null || serialSet.getSerialNumbers().isEmpty()) {
            log.error("Serial set validation failed. No serial numbers for export: {}", serialSetName);
            throw new CustomCsvExportException(NO_SERIAL_NUMBERS_ERROR_MESSAGE);
        }

        if (serialSet.getSerialNumbers().size() < serialSet.getQuantity()) {
            log.error("Serial set validation failed. Incomplete generation for export: {}", serialSetName);
            throw new CustomCsvExportException(INCOMPLETE_GENERATION_ERROR_MESSAGE);
        }

        log.info("Serial set fetched and validated successfully for export: {}", serialSetName);
        return serialSet;
    }

    /**
     * Exports serial numbers to a CSV file for the given serial set.
     *
     * @param serialSet The serial set to export
     * @throws IOException If an I/O error occurs during the export
     */
    private void exportToCsv(SerialSet serialSet) throws IOException {
        log.info("Exporting serial numbers to CSV file for serial set: {}", serialSet.getName());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "exported_serial_numbers_" + serialSet.getName() + "_" + timeStamp + ".csv";
        Path filePath = Paths.get(exportDirectory, fileName);

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.append("Serial Number\n");

            for (SerialNumber serialNumber : serialSet.getSerialNumbers()) {
                writer.append(serialNumber.getValue());
                writer.append("\n");
            }

            writer.flush();
        }

        log.info("Serial numbers exported to CSV file successfully for serial set: {}", serialSet.getName());
    }
}
