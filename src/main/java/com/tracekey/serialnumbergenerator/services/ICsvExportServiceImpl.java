package com.tracekey.serialnumbergenerator.services;

import com.tracekey.serialnumbergenerator.entities.SerialNumber;
import com.tracekey.serialnumbergenerator.entities.SerialSet;
import com.tracekey.serialnumbergenerator.exceptions.CsvExportException;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class ICsvExportServiceImpl implements ICsvExportService{


        public void exportToCsv(SerialSet serialSet) {
            String fileName = "exported_serial_numbers_" + serialSet.getName() + ".csv";

            try (FileWriter writer = new FileWriter(fileName)) {
                // Add CSV header if needed
                writer.append("Serial Number\n");

                for (SerialNumber serialNumber : serialSet.getSerialNumbers()) {
                    writer.append(serialNumber.getValue());
                    writer.append("\n");
                }

                writer.flush();
            } catch (IOException e) {
                throw new CsvExportException("Error exporting serial numbers to CSV", e);
            }
        }
    }
