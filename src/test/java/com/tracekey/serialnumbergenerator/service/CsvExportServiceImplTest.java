package com.tracekey.serialnumbergenerator.service;

import com.tracekey.serialnumbergenerator.conf.TestDatabaseConfig;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link CsvExportServiceImpl}.
 */
@Import(TestDatabaseConfig.class)
@ActiveProfiles("test")
class CsvExportServiceImplTest {

    private static final String SERIAL_SET_NAME = "TestSet";

    @Mock
    private SerialSetRepository serialSetRepository;

    @Mock
    private SerialNumberRepository serialNumberRepository;

    @InjectMocks
    private CsvExportServiceImpl csvExportService;

    private static final String EXPORT_DIRECTORY = "src/main/resources/";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(csvExportService, "exportDirectory", EXPORT_DIRECTORY);
    }

    @Test
    void shouldExportSerialNumbersToCSV() {
        SerialSet serialSet = createTestSerialSet();
        when(serialSetRepository.findByName(SERIAL_SET_NAME)).thenReturn(java.util.Optional.of(serialSet));
        assertDoesNotThrow(() -> csvExportService.exportSerialNumbersToCSV(SERIAL_SET_NAME));
    }

    @Test
    void shouldThrowExceptionIfSerialSetNotFoundForExport() {
        when(serialSetRepository.findByName(SERIAL_SET_NAME)).thenReturn(Optional.empty());
        assertThrows(CustomCsvExportException.class,
                () -> csvExportService.exportSerialNumbersToCSV(SERIAL_SET_NAME),
                "Serial set not found for export");
    }

    @Test
    void shouldThrowExceptionIfNoSerialNumbersForExport() {
        when(serialSetRepository.findByName(SERIAL_SET_NAME)).thenReturn(Optional.of(createTestSerialSetWithoutSerialNumbers()));
        assertThrows(CustomCsvExportException.class,
                () -> csvExportService.exportSerialNumbersToCSV(SERIAL_SET_NAME),
                "No serial numbers created for this serial set");
    }

    @Test
    void shouldThrowExceptionIfIncompleteGenerationForExport() {
        SerialSet serialSet = createTestSerialSet();
        List<SerialNumber> serialNumbers = createTestSerialNumbers(serialSet);
        serialSet.setSerialNumbers(new ArrayList<>(serialNumbers.subList(0, serialNumbers.size() - 1)));
        when(serialSetRepository.findByName(SERIAL_SET_NAME)).thenReturn(Optional.of(serialSet));

        assertThrows(CustomCsvExportException.class,
                () -> csvExportService.exportSerialNumbersToCSV(SERIAL_SET_NAME),
                "Serial set numbers generation is incomplete");
    }

    private SerialSet createTestSerialSetWithoutSerialNumbers() {
        return saveSerialSet();
    }

    private SerialSet createTestSerialSet() {
        SerialSet serialSet = saveSerialSet();
        serialSet.setSerialNumbers(createTestSerialNumbers(serialSet));
        return serialSet;
    }

    private List<SerialNumber> createTestSerialNumbers(SerialSet serialSet) {
        List<SerialNumber> serialNumbers = new ArrayList<>();
        for (int i = 1; i <= serialSet.getQuantity(); i++) {
            serialNumbers.add(new SerialNumber("SN" + i, serialSet));
        }
        return saveSerialNumbers(serialNumbers);
    }

    private SerialSet saveSerialSet() {
        return new SerialSet().setName(SERIAL_SET_NAME).setQuantity(5);
    }

    private List<SerialNumber> saveSerialNumbers(List<SerialNumber> serialNumbers) {
        when(serialNumberRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        return serialNumberRepository.saveAll(serialNumbers);
    }

}
