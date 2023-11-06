package com.tracekey.serialnumbergenerator.service;

import com.tracekey.serialnumbergenerator.entity.SerialNumber;
import com.tracekey.serialnumbergenerator.entity.SerialSet;
import com.tracekey.serialnumbergenerator.exception.SerialSetException;
import com.tracekey.serialnumbergenerator.mapper.SerialSetMapper;
import com.tracekey.serialnumbergenerator.dto.SerialSetRequest;
import com.tracekey.serialnumbergenerator.dto.SerialSetResponse;
import com.tracekey.serialnumbergenerator.repository.SerialNumberRepository;
import com.tracekey.serialnumbergenerator.repository.SerialSetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of the SerialSetService interface.
 */
@Service
@Slf4j
public class SerialSetServiceImpl implements ISerialSetService {

    /**
     * Character set containing numeric characters.
     */
    private static final String NUMERIC_CHARACTERS = "0123456789";

    /**
     * Character set containing lowercase alphabetical characters.
     */
    private static final String LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Character set containing uppercase alphabetical characters.
     */
    private static final String UPPERCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Error message for invalid length configuration.
     */
    private static final String LENGTH_CONFIG_ERROR_MESSAGE = "Invalid length configuration";

    /**
     * Error message for a duplicate serial set name.
     */
    private static final String DUPLICATE_NAME_ERROR_MESSAGE = "Serial set with the same name already exists";

    /**
     * Error message for exceeding the maximum limit of serial numbers.
     */
    private static final String MAX_LIMIT_ERROR_MESSAGE = "Request exceeds the maximum limit of serial numbers";

    /**
     * Template for error message when a serial set is not found.
     */
    private static final String NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Serial set not found with ID: %s";

    /**
     * Repository for serial sets.
     */
    private final SerialSetRepository serialSetRepository;

    /**
     * Repository for serial numbers.
     */
    private final SerialNumberRepository serialNumberRepository;

    /**
     * Mapper for serial numbers.
     */
    private final SerialSetMapper serialSetMapper;


    /**
     * Minimum allowed serial length loaded from properties.
     */
    @Value("${serialNumber.min.serial.length}")
    private int minSerialLength;

    /**
     * Maximum allowed serial length loaded from properties.
     */
    @Value("${serialNumber.max.serial.length}")
    private int maxSerialLength;

    /**
     * Maximum allowed random length loaded from properties.
     */
    @Value("${serialNumber.max.random.length}")
    private int maxRandomLength;

    /**
     * Maximum allowed serial quantity loaded from properties.
     */
    @Value("${serialSet.max.serial.quantity}")
    private int maxSerialQuantity;

    /**
     * Batch size for processing serial numbers loaded from properties.
     */
    @Value("${serialSet.batchSize}")
    private int batchSize;


    /**
     * Constructor for injecting repositories.
     *
     * @param serialSetRepository     Repository for serial sets
     * @param serialNumberRepository  Repository for serial numbers
     * @param serialSetMapper         Mapper for serial numbers
     */
    public SerialSetServiceImpl(final SerialSetRepository serialSetRepository, final SerialNumberRepository serialNumberRepository,final SerialSetMapper serialSetMapper) {
        this.serialSetRepository = serialSetRepository;
        this.serialNumberRepository = serialNumberRepository;
        this.serialSetMapper=serialSetMapper;
    }

    /**
     * Creates a new serial set.
     *
     * @return The created serial set
     */
    @Override
    public SerialSetResponse createSerialSet(final SerialSetRequest serialSetRequest) {
        log.info("Creating serial set: {}", serialSetRequest.getName());
        SerialSet serialSet = serialSetMapper.mapRequestDtoToEntity(serialSetRequest);
        validateSerialSetConfiguration(serialSet);
        validateSerialSet(serialSet);
        saveSerialSet(serialSet);
        generateSerialNumbersAsync(serialSet);
        log.info("Serial set created successfully: {}", serialSet.getName());
        return serialSetMapper.mapEntityToResponseDto(serialSet);
    }

    /**
     * Retrieves all serial sets.
     *
     * @return List of all serial sets
     */
    @Override
    public List<SerialSetResponse> getAllSerialSets() {
        log.info("Fetching all serial sets");
        List<SerialSet> serialSets = serialSetRepository.findAll();
        log.info("Fetched {} serial sets", serialSets.size());

        return serialSets.stream()
                .map(serialSetMapper::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a serial set by its name.
     *
     * @param name The ID of the serial set to retrieve
     * @return The retrieved serial set
     */
    @Override
    public SerialSetResponse getSerialSetByName(final String name) {
        log.info("Fetching serial set: {}", name);

        SerialSet serialSet = serialSetRepository.findByName(name);

        if (serialSet == null) {
            throw new SerialSetException(String.format(NOT_FOUND_ERROR_MESSAGE_TEMPLATE, name));
        }

        log.info("Fetched serial set: {}", serialSet.getName());
        return serialSetMapper.mapEntityToResponseDto(serialSet);
    }



    /**
     * Deletes a serial set by its name.
     *
     * @param name The Name of the serial set to delete
     * @return True if deletion is successful, false otherwise
     */
    @Override
    public boolean deleteSerialSetByName(final String name) {
        log.info("Deleting serial set by : {}", name);
        if (name != null) {
            final SerialSet serialSet = serialSetRepository.findByName(name);
            if (serialSet!=null) {
                final List<SerialNumber> serialNumbers = serialSet.getSerialNumbers();
                serialNumberRepository.deleteAll(serialNumbers);
                serialSetRepository.delete(serialSet);
                log.info("Deleted serial set successfully: {}", name);
                return true;
            }
        }
        log.warn("Serial set deletion failed. Serial set not found : {}", name);
        return false;
    }

    /**
     * Asynchronously generates serial numbers for a serial set.
     *
     * @param serialSet The serial set for which to generate serial numbers
     * @return CompletableFuture indicating completion of the generation process
     */
    @Async
    @Override
    public CompletableFuture<Void> generateSerialNumbersAsync(final SerialSet serialSet) {
        log.info("Generating serial numbers asynchronously for serial set started: {}", serialSet.getName());
         return CompletableFuture.runAsync(() -> generateSerialNumbers(serialSet))
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Serial numbers generated successfully for serial set: {}", serialSet.getName());
                    } else {
                        log.error("Error generating serial numbers for serial set: {}", serialSet.getName(), ex);
                    }
                });
    }

    /**
     * Gets the character pool based on the serial set configuration.
     *
     * @param serialSet The serial set for which to get the character pool
     * @return The character pool
     */
    @Override
    public String getCharacterPool(final SerialSet serialSet) {
        log.debug("Getting character pool for serial set: {}", serialSet.getName());
        final StringBuilder characters = new StringBuilder();
        if (serialSet.isNumber()) {
            characters.append(NUMERIC_CHARACTERS);
        }
        if (serialSet.isLowerCase()) {
            characters.append(LOWERCASE_CHARACTERS);
        }
        if (serialSet.isUpperCase()) {
            characters.append(UPPERCASE_CHARACTERS);
        }
        log.debug("Character pool for serial set {}: {}", serialSet.getName(), characters);
        return characters.toString();
    }

    /**
     * Removes excluded characters from a string.
     *
     * @param input      The input string
     * @param exclusions The characters to exclude
     * @return The input string with exclusions removed
     */
    @Override
    public String removeExclusions(String input, final String exclusions) {
        log.debug("Removing exclusions for input: {}, exclusions: {}", input, exclusions);
        if (input == null || exclusions == null) {
            return input;
        }
        for (final char exclusion : exclusions.toCharArray()) {
            input = input.replace(String.valueOf(exclusion), "");
        }
        log.debug("Result after removing exclusions: {}", input);
        return input;
    }

    /**
     * Generates and saves serial numbers for a serial set.
     *
     * @param serialSet The serial set for which to generate and save serial numbers
     */
    @Override
    public void generateSerialNumbers(final SerialSet serialSet) {
        log.info("Generating and saving serial numbers for serial set: {}", serialSet.getName());
        int remainingSerials = serialSet.getQuantity();
        final Set<String> uniqueSerials = new HashSet<>();

        while (remainingSerials > 0) {
            final int currentBatchSize = Math.min(remainingSerials, batchSize);
            String characters = getCharacterPool(serialSet);
            final String cleanCharacters = removeExclusions(characters, serialSet.getExclusions());

            final List<SerialNumber> generatedSerials = IntStream.range(0, currentBatchSize)
                    .mapToObj(i -> {
                        String generatedSerial;
                        do {
                            generatedSerial = generateSingleSerial(serialSet, cleanCharacters);
                        } while (!uniqueSerials.add(generatedSerial));

                       return new SerialNumber(generatedSerial, serialSet);
                    })
                    .collect(Collectors.toList());

            serialNumberRepository.saveAll(generatedSerials);

            remainingSerials -= currentBatchSize;
        }

        uniqueSerials.clear();
        log.info("Generated and saved all serial numbers for serial set: {}", serialSet.getName());
    }

    /**
     * Generates a single serial number based on the serial set configuration.
     *
     * @param serialSet The serial set for which to generate a serial number
     * @return The generated serial number
     */
    @Override
    public String generateSingleSerial(final SerialSet serialSet,String characters) {
        log.info("Generating single serial for serial set: {}", serialSet.getName());
         final StringBuilder generatedSerial = new StringBuilder();
        final SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < serialSet.getSerialLength(); i++) {
            final char randomChar = characters.charAt(secureRandom.nextInt(characters.length()));
            generatedSerial.append(randomChar);
        }
        log.info("Generated single serial: {}", generatedSerial);
        return generatedSerial.toString();
    }

    /**
     * Validates and saves a serial set.
     *
     * @param serialSet The serial set to validate and save
     */
    @Override
    public boolean validateSerialSet(final SerialSet serialSet) {
        log.info("Validating and saving serial set: {}", serialSet.getName());
        final SerialSet existingSerialSet = serialSetRepository.findByName(serialSet.getName());
        if (existingSerialSet != null) {
            log.error("Serial set validation failed. Duplicate name found: {}", serialSet.getName());
            throw new SerialSetException(DUPLICATE_NAME_ERROR_MESSAGE);
        }
        final int totalSerials = serialSet.getQuantity();
        if (totalSerials > maxSerialQuantity) {
            log.error("Serial set validation failed. Exceeds maximum limit of serial numbers. Serial set: {}", serialSet.getName());
            throw new SerialSetException(MAX_LIMIT_ERROR_MESSAGE);
        }


        log.info("Serial set saved successfully: {}", serialSet.getName());
        return true;
    }
    @Override
    public SerialSet saveSerialSet(SerialSet serialSet){
        return serialSetRepository.save(serialSet);
    }

    /**
     * Validates the configuration of a serial set.
     *
     * @param serialSet The serial set to validate
     */
    @Override
    public boolean validateSerialSetConfiguration(final SerialSet serialSet) {
        log.info("Validating configuration of serial set: {}", serialSet.getName());
        if (serialSet.isConfiguration()) {
            if (serialSet.getSerialLength() < minSerialLength || serialSet.getSerialLength() > maxSerialLength) {
                log.error("Serial set configuration validation failed. Invalid length configuration. Serial set: {}", serialSet.getName());
                throw new SerialSetException(LENGTH_CONFIG_ERROR_MESSAGE);
            }
        } else {
            if (maxRandomLength == 0) {
                log.error("Serial set configuration validation failed. maxRandomLength should be a non-zero value. Serial set: {}", serialSet.getName());
                throw new SerialSetException("maxRandomLength should be a non-zero value");
            }
            final SecureRandom secureRandom = new SecureRandom();
            serialSet.setSerialLength(secureRandom.nextInt(maxRandomLength) + 1);
            serialSet.setNumber(true);
        }return true;
    }
}
