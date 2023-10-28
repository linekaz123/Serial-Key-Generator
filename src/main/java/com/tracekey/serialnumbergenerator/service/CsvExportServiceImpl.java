package com.tracekey.serialnumbergenerator.service;

import com.tracekey.serialnumbergenerator.entity.SerialNumber;
import com.tracekey.serialnumbergenerator.entity.SerialSet;
import com.tracekey.serialnumbergenerator.exception.CustomCsvExportException;
import com.tracekey.serialnumbergenerator.repository.SerialSetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CsvExportServiceImpl implements ICsvExportService {

    @Value("${csv.export.directory}")
    private String exportDirectory;
    private static final String EXPORT_ERROR_MESSAGE = "Error exporting serial numbers to CSV";
    private static final String NOT_FOUND_ERROR_MESSAGE = "Serial set not found for export";
    private static final String NO_SERIAL_NUMBERS_ERROR_MESSAGE = "No serial numbers created for this serial set";
    private static final String INCOMPLETE_GENERATION_ERROR_MESSAGE = "Serial set numbers generation is incomplete";

    private final SerialSetRepository serialSetRepository;

    public CsvExportServiceImpl(SerialSetRepository serialSetRepository) {
        this.serialSetRepository = serialSetRepository;
    }

    @Override
    public boolean exportSerialNumbersToCSV(String serialSetName) {
        SerialSet serialSet = getValidSerialSet(serialSetName);

        try {
            exportToCsv(serialSet);
            return true;
        } catch (IOException e) {
            throw new CustomCsvExportException(EXPORT_ERROR_MESSAGE, e);
        }
    }

    private SerialSet getValidSerialSet(String serialSetName) {
        SerialSet serialSet = serialSetRepository.findByName(serialSetName);

        if (serialSet == null) {
            throw new CustomCsvExportException(NOT_FOUND_ERROR_MESSAGE);
        }

        if (serialSet.getSerialNumbers() == null || serialSet.getSerialNumbers().isEmpty()) {
            throw new CustomCsvExportException(NO_SERIAL_NUMBERS_ERROR_MESSAGE);
        }

        if (serialSet.getSerialNumbers().size() < serialSet.getQuantity()) {
            throw new CustomCsvExportException(INCOMPLETE_GENERATION_ERROR_MESSAGE);
        }

        return serialSet;
    }

    private void exportToCsv(SerialSet serialSet) throws IOException {
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
    }
}
