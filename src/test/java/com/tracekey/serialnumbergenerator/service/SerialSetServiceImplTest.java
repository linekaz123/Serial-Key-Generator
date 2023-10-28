package com.tracekey.serialnumbergenerator.service;

import com.tracekey.serialnumbergenerator.entity.SerialSet;
import com.tracekey.serialnumbergenerator.exception.SerialSetException;
import com.tracekey.serialnumbergenerator.repository.SerialSetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;


class SerialSetServiceImplTest {

    private static final String NAME = "TestSet";

    @Mock
    private SerialSetRepository serialSetRepository;

    @InjectMocks
    private SerialSetServiceImpl serialSetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(serialSetService, "minSerialLength", 10);
        ReflectionTestUtils.setField(serialSetService, "maxSerialLength", 20);
        ReflectionTestUtils.setField(serialSetService, "maxRandomLength", 12);
        ReflectionTestUtils.setField(serialSetService, "maxSerialQuantity", 10000);
        ReflectionTestUtils.setField(serialSetService, "batchSize", 50);
    }

    @Test
    void shouldThrowExceptionIfExceedsMaxQuantityOnValidation() {
        SerialSet inputSerialSet = createSerialSet(NAME, 15000);

        lenient().when(serialSetRepository.findByName(NAME)).thenReturn(null);

        assertThrows(SerialSetException.class, () -> serialSetService.validateAndSaveSerialSet(inputSerialSet));
    }

    @Test
    void shouldValidateAndSaveSerialSetSuccessfully() {
        SerialSet inputSerialSet = createSerialSet(NAME, 5);

        lenient().when(serialSetRepository.findByName(NAME)).thenReturn(null);

        assertDoesNotThrow(() -> serialSetService.validateAndSaveSerialSet(inputSerialSet));
    }

    @Test
    void shouldThrowExceptionIfInvalidSerialLengthOnValidation() {
        SerialSet inputSerialSet = createConfiguredSerialSet(NAME, 5);

        lenient().when(serialSetRepository.findByName(NAME)).thenReturn(null);

        assertThrows(SerialSetException.class, () -> serialSetService.validateSerialSetConfiguration(inputSerialSet));
    }

    @Test
    void shouldGenerateAndSaveSerialNumbersAsync() {
        SerialSet serialSet = createSerialSet(NAME, 100);

        CompletableFuture<Void> result = serialSetService.generateSerialNumbersAsync(serialSet);

        assertNotNull(result);
    }

    @Test
    void shouldGetCharacterPool() {
        SerialSet serialSet = createCharacterPoolSerialSet();

        String characterPool = serialSetService.getCharacterPool(serialSet);

        assertNotNull(characterPool);
        assertTrue(characterPool.contains("0123456789"));
        assertTrue(characterPool.contains("abcdefghijklmnopqrstuvwxyz"));
        assertTrue(characterPool.contains("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    @Test
    void shouldRemoveExclusions() {
        String input = "A1B2C3D4";
        String exclusions = "123";

        String result = serialSetService.removeExclusions(input, exclusions);

        assertNotNull(result);
        assertEquals("ABCD4", result);
    }

    @Test
    void shouldValidateSerialSetConfigurationSuccessfully() {
        SerialSet serialSet = createConfiguredSerialSet(false, 15);

        assertDoesNotThrow(() -> serialSetService.validateSerialSetConfiguration(serialSet));
    }

    @Test
    void shouldThrowExceptionIfConfigurationLengthBelowMin() {
        SerialSet serialSet = createConfiguredSerialSet(true, 5);

        assertThrows(SerialSetException.class, () -> serialSetService.validateSerialSetConfiguration(serialSet));
    }

    @Test
    void shouldThrowExceptionIfConfigurationLengthAboveMax() {
        SerialSet serialSet = createConfiguredSerialSet(true, 25);

        assertThrows(SerialSetException.class, () -> serialSetService.validateSerialSetConfiguration(serialSet));
    }

    @Test
    void shouldValidateDefaultConfigurationSuccessfully() {
        SerialSet serialSet = createConfiguredSerialSet(false, 0);

        assertDoesNotThrow(() -> serialSetService.validateSerialSetConfiguration(serialSet));
    }

    private SerialSet createSerialSet(String name, int quantity) {
        SerialSet serialSet = new SerialSet();
        serialSet.setName(name);
        serialSet.setQuantity(quantity);
        return serialSet;
    }

    private SerialSet createConfiguredSerialSet(String name, int serialLength) {
        SerialSet serialSet = createSerialSet(name, 0);
        serialSet.setConfiguration(true);
        serialSet.setSerialLength(serialLength);
        serialSet.setNumber(true);
        return serialSet;
    }

    private SerialSet createCharacterPoolSerialSet() {
        SerialSet serialSet = new SerialSet();
        serialSet.setNumber(true);
        serialSet.setLowerCase(true);
        serialSet.setUpperCase(true);
        return serialSet;
    }

    private SerialSet createConfiguredSerialSet(boolean configuration, int serialLength) {
        SerialSet serialSet = new SerialSet();
        serialSet.setConfiguration(configuration);
        serialSet.setSerialLength(serialLength);
        return serialSet;
    }
}
