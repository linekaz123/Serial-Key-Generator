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
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CsvExportService interface for exporting serial numbers to CSV.
 */
@Service
@Slf4j
public class CsvExportServiceImpl implements ICsvExportService {

    /**
     * Directory for exporting CSV files property.
     */
    @Value("${csv.export.directory}")
    private String exportDirectory;

    private static final String EXPORT_ERROR_MESSAGE = "Error exporting serial numbers to CSV";
    private static final String NOT_FOUND_ERROR_MESSAGE = "Serial set not found for export";
    private static final String INCOMPLETE_GENERATION_ERROR_MESSAGE = "Serial set numbers generation is incomplete";

    private final SerialSetRepository serialSetRepository;

    public CsvExportServiceImpl(SerialSetRepository serialSetRepository) {
        this.serialSetRepository = serialSetRepository;
    }

    /**
     * Exports serial numbers to CSV for a given serial set name.
     *
     * @param serialSetName The name of the serial set to export
     *
     */
    @Override
    public void exportSerialNumbersToCSV(String serialSetName) {
        log.info("Exporting serial numbers to CSV for serial set: {}", serialSetName);

        try {
            SerialSet serialSet = getValidSerialSet(serialSetName);
            exportToCsv(serialSet);
            log.info("Serial numbers exported to CSV successfully for serial set: {}", serialSetName);
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

        Optional<SerialSet> optionalSerialSet = serialSetRepository.findByName(serialSetName);

        SerialSet serialSet = optionalSerialSet.orElseThrow(() -> {
            log.error("Serial set validation failed. Not found for export: {}", serialSetName);
            return new CustomCsvExportException(NOT_FOUND_ERROR_MESSAGE);
        });

        List<SerialNumber> serialNumbers = serialSet.getSerialNumbers();
        int quantity = serialSet.getQuantity();

        if (serialNumbers == null || serialNumbers.size() < quantity) {
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
