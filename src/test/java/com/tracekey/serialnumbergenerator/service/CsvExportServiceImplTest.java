package com.tracekey.serialnumbergenerator.service;

import com.tracekey.serialnumbergenerator.entity.SerialNumber;
import com.tracekey.serialnumbergenerator.entity.SerialSet;
import com.tracekey.serialnumbergenerator.exception.CustomCsvExportException;
import com.tracekey.serialnumbergenerator.repository.SerialNumberRepository;
import com.tracekey.serialnumbergenerator.repository.SerialSetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Test class for CsvExportServiceImpl
class CsvExportServiceImplTest {

    // Test constant
    private static final String SERIAL_SET_NAME = "TestSet";

    // Mocked repositories
    @Mock
    private SerialSetRepository serialSetRepository;

    @Mock
    private SerialNumberRepository serialNumberRepository;

    // Injecting mocks into CsvExportServiceImpl
    @InjectMocks
    private CsvExportServiceImpl csvExportService;

    // Setting up the test environment before each test
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Setting up reflection test utils to inject private fields
        ReflectionTestUtils.setField(csvExportService, "exportDirectory", "src/main/resources/");
    }

    // Testing the successful export of serial numbers to CSV
    @Test
    void shouldExportSerialNumbersToCSV() throws IOException {
        SerialSet serialSet = createTestSerialSet();
        when(serialSetRepository.findByName(SERIAL_SET_NAME)).thenReturn(serialSet);

        boolean result = csvExportService.exportSerialNumbersToCSV(SERIAL_SET_NAME);
        assertTrue(result);
    }

    // Testing if an exception is thrown when the serial set is not found for export
    @Test
    void shouldThrowExceptionIfSerialSetNotFoundForExport() {
        when(serialSetRepository.findByName(SERIAL_SET_NAME)).thenReturn(null);

        CustomCsvExportException exception = assertThrows(CustomCsvExportException.class,
                () -> csvExportService.exportSerialNumbersToCSV(SERIAL_SET_NAME));

        assertEquals("Serial set not found for export", exception.getMessage());
    }

    // Testing if an exception is thrown when no serial numbers are found for export
    @Test
    void shouldThrowExceptionIfNoSerialNumbersForExport() {
        SerialSet serialSet = createTestSerialSetWithoutSerialNumbers();
        when(serialSetRepository.findByName(SERIAL_SET_NAME)).thenReturn(serialSet);
        when(serialNumberRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CustomCsvExportException exception = assertThrows(CustomCsvExportException.class,
                () -> csvExportService.exportSerialNumbersToCSV(SERIAL_SET_NAME));

        assertEquals("No serial numbers created for this serial set", exception.getMessage());
    }

    // Testing if an exception is thrown when serial number generation is incomplete for export
    @Test
    void shouldThrowExceptionIfIncompleteGenerationForExport() {
        SerialSet serialSet = createTestSerialSet();
        List<SerialNumber> serialNumbers = createTestSerialNumbers(serialSet);
        serialSet.setSerialNumbers(new ArrayList<>(serialNumbers.subList(0, serialNumbers.size() - 1)));

        when(serialSetRepository.findByName(SERIAL_SET_NAME)).thenReturn(serialSet);
        when(serialNumberRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CustomCsvExportException exception = assertThrows(CustomCsvExportException.class,
                () -> csvExportService.exportSerialNumbersToCSV(SERIAL_SET_NAME));

        assertEquals("Serial set numbers generation is incomplete", exception.getMessage());
    }

    // Helper method to create a SerialSet without serial numbers
    private SerialSet createTestSerialSetWithoutSerialNumbers() {
        SerialSet serialSet = new SerialSet();
        serialSet.setName(SERIAL_SET_NAME);
        serialSet.setQuantity(5);
        when(serialSetRepository.save(any(SerialSet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        return serialSetRepository.save(serialSet);
    }

    // Helper method to create a complete SerialSet with serial numbers
    private SerialSet createTestSerialSet() {
        SerialSet serialSet = new SerialSet();
        serialSet.setName(SERIAL_SET_NAME);
        serialSet.setQuantity(5);
        serialSet.setSerialNumbers(createTestSerialNumbers(serialSet));
        when(serialSetRepository.save(any(SerialSet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        return serialSetRepository.save(serialSet);
    }

    // Helper method to create a list of SerialNumbers for a given SerialSet
    private List<SerialNumber> createTestSerialNumbers(SerialSet serialSet) {
        List<SerialNumber> serialNumbers = new ArrayList<>();
        for (int i = 1; i <= serialSet.getQuantity(); i++) {
            SerialNumber serialNumber = new SerialNumber("SN" + i);
            serialNumber.setSerialSet(serialSet);
            serialNumbers.add(serialNumber);
        }
        when(serialNumberRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        return serialNumberRepository.saveAll(serialNumbers);
    }
}
