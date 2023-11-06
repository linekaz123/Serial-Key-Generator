package com.tracekey.serialnumbergenerator.service;

import com.tracekey.serialnumbergenerator.entity.SerialNumber;
import com.tracekey.serialnumbergenerator.entity.SerialSet;
import com.tracekey.serialnumbergenerator.exception.SerialSetException;
import com.tracekey.serialnumbergenerator.repository.SerialSetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link SerialSetServiceImpl}.
 */
class SerialSetServiceImplTest {

    private static final String NAME = "TestSet";

    @Mock
    private SerialSetRepository serialSetRepository;

    @InjectMocks
    private SerialSetServiceImpl serialSetService;

    /**
     * Minimum allowed serial length property.
     */
    private static final int MIN_SERIAL_LENGTH = 10;

    /**
     * Maximum allowed serial length property.
     */
    private static final int MAX_SERIAL_LENGTH = 20;

    /**
     * Maximum allowed random length property.
     */
    private static final int MAX_RANDOM_LENGTH = 12;

    /**
     * Maximum allowed serial quantity property.
     */
    private static final int MAX_SERIAL_QUANTITY = 10000;

    /**
     * Batch size property for processing serial numbers.
     */
    private static final int BATCH_SIZE = 50;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setting up reflection test utils to inject private fields
        ReflectionTestUtils.setField(serialSetService, "minSerialLength", MIN_SERIAL_LENGTH);
        ReflectionTestUtils.setField(serialSetService, "maxSerialLength", MAX_SERIAL_LENGTH);
        ReflectionTestUtils.setField(serialSetService, "maxRandomLength", MAX_RANDOM_LENGTH);
        ReflectionTestUtils.setField(serialSetService, "maxSerialQuantity", MAX_SERIAL_QUANTITY);
        ReflectionTestUtils.setField(serialSetService, "batchSize", BATCH_SIZE);
    }

    /**
     * Testing if an exception is thrown when the quantity exceeds the maximum allowed.
     */
    @Test
    void shouldThrowExceptionIfExceedsMaxQuantityOnValidation() {
        SerialSet inputSerialSet = createSerialSet(NAME, 15000);

        lenient().when(serialSetRepository.findByName(NAME)).thenReturn(null);

        assertThrows(SerialSetException.class, () -> serialSetService.validateSerialSet(inputSerialSet));
    }

    /**
     * Testing successful validation of a SerialSet.
     */
    @Test
    void shouldValidateAndSaveSerialSetSuccessfully() {
        SerialSet inputSerialSet = createSerialSet(NAME, 5);

        lenient().when(serialSetRepository.findByName(NAME)).thenReturn(null);

        assertDoesNotThrow(() -> serialSetService.validateSerialSet(inputSerialSet));
    }

    /**
     * Testing if an exception is thrown when the serial length is invalid during validation.
     */
    @Test
    void shouldThrowExceptionIfInvalidSerialLengthOnValidation() {
        SerialSet inputSerialSet = createConfiguredSerialSet(NAME, 5);

        lenient().when(serialSetRepository.findByName(NAME)).thenReturn(null);

        assertThrows(SerialSetException.class, () -> serialSetService.validateSerialSetConfiguration(inputSerialSet));
    }

    /**
     * Testing asynchronous generation and saving of serial numbers.
     */
    @Test
    void shouldGenerateSerialNumbersAsync() {

        SerialSet serialSet = createSerialSet(NAME, 100);

        CompletableFuture<Void> future = serialSetService.generateSerialNumbersAsync(serialSet);
        future.join();

        assertEquals(100, serialSet.getSerialNumbers().size());
    }
    /**
     * Testing the generation of character pool for serial numbers based on the configuration.
     */
    @Test
    void shouldGetCharacterPool() {
        SerialSet serialSet = createCharacterPoolSerialSet();

        String characterPool = serialSetService.getCharacterPool(serialSet);

        assertNotNull(characterPool);
        assertTrue(characterPool.contains("0123456789"));
        assertTrue(characterPool.contains("abcdefghijklmnopqrstuvwxyz"));
        assertTrue(characterPool.contains("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    /**
     * Testing the removal of exclusions from a string.
     */
    @Test
    void shouldRemoveExclusions() {
        String input = "A1B2C3D4";
        String exclusions = "123";

        String result = serialSetService.removeExclusions(input, exclusions);

        assertNotNull(result);
        assertEquals("ABCD4", result);
    }

    /**
     * Testing successful validation of SerialSet configuration within the given conditions.
     */
    @Test
    void shouldValidateSerialSetConfigurationSuccessfully() {
        SerialSet serialSet = createConfiguredSerialSet(false, 15);

        assertDoesNotThrow(() -> serialSetService.validateSerialSetConfiguration(serialSet));
    }

    /**
     * Testing if an exception is thrown when the configuration length is below the minimum.
     */
    @Test
    void shouldThrowExceptionIfConfigurationLengthBelowMin() {
        SerialSet serialSet = createConfiguredSerialSet(true, 5);

        assertThrows(SerialSetException.class, () -> serialSetService.validateSerialSetConfiguration(serialSet));
    }

    /**
     * Testing if an exception is thrown when the configuration length is above the maximum.
     */
    @Test
    void shouldThrowExceptionIfConfigurationLengthAboveMax() {
        SerialSet serialSet = createConfiguredSerialSet(true, 25);

        assertThrows(SerialSetException.class, () -> serialSetService.validateSerialSetConfiguration(serialSet));
    }

    /**
     * Testing successful validation of default SerialSet configuration.
     */
    @Test
    void shouldValidateDefaultConfigurationSuccessfully() {
        SerialSet serialSet = createConfiguredSerialSet(false, 0);

        assertDoesNotThrow(() -> serialSetService.validateSerialSetConfiguration(serialSet));
    }

    /**
     * Helper method to create a SerialSet with given name and quantity.
     */
    private SerialSet createSerialSet(String name, int quantity) {
        SerialSet serialSet = new SerialSet();
        serialSet.setName(name);
        serialSet.setQuantity(quantity);
        return serialSet;
    }

    /**
     * Helper method to create a configured SerialSet with given name and serial length.
     */
    private SerialSet createConfiguredSerialSet(String name, int serialLength) {
        SerialSet serialSet = createSerialSet(name, 0);
        serialSet.setConfiguration(true);
        serialSet.setSerialLength(serialLength);
        serialSet.setNumber(true);
        return serialSet;
    }

    /**
     * Helper method to create a SerialSet with configured character pool.
     */
    private SerialSet createCharacterPoolSerialSet() {
        SerialSet serialSet = new SerialSet();
        serialSet.setNumber(true);
        serialSet.setLowerCase(true);
        serialSet.setUpperCase(true);
        return serialSet;
    }

    /**
     * Helper method to create a configured SerialSet with given configuration and serial length.
     */
    private SerialSet createConfiguredSerialSet(boolean configuration, int serialLength) {
        SerialSet serialSet = new SerialSet();
        serialSet.setConfiguration(configuration);
        serialSet.setSerialLength(serialLength);
        return serialSet;
    }

}
