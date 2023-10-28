package com.tracekey.serialnumbergenerator.service;

import com.tracekey.serialnumbergenerator.entity.SerialNumber;
import com.tracekey.serialnumbergenerator.entity.SerialSet;
import com.tracekey.serialnumbergenerator.exception.SerialSetException;
import com.tracekey.serialnumbergenerator.repository.SerialNumberRepository;
import com.tracekey.serialnumbergenerator.repository.SerialSetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class SerialSetServiceImpl implements ISerialSetService {

    private static final String NUMERIC_CHARACTERS = "0123456789";
    private static final String LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LENGTH_CONFIG_ERROR_MESSAGE = "Invalid length configuration";
    private static final String DUPLICATE_NAME_ERROR_MESSAGE = "Serial set with the same name already exists";
    private static final String MAX_LIMIT_ERROR_MESSAGE = "Request exceeds the maximum limit of serial numbers";
    private static final String NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Serial set not found with ID: %d";

    private final SerialSetRepository serialSetRepository;
    private final SerialNumberRepository serialNumberRepository;

    @Value("${serialNumber.min.serial.length}")
    private int minSerialLength;

    @Value("${serialNumber.max.serial.length}")
    private int maxSerialLength;

    @Value("${serialNumber.max.random.length}")
    private int maxRandomLength;

    @Value("${serialSet.max.serial.quantity}")
    private int maxSerialQuantity;

    @Value("${serialSet.batchSize}")
    private int batchSize;

    public SerialSetServiceImpl(SerialSetRepository serialSetRepository, SerialNumberRepository serialNumberRepository) {
        this.serialSetRepository = serialSetRepository;
        this.serialNumberRepository = serialNumberRepository;
    }

    @Override
    public SerialSet createSerialSet(SerialSet serialSet) {
        validateSerialSetConfiguration(serialSet);
        validateAndSaveSerialSet(serialSet);
        generateSerialNumbersAsync(serialSet);
        return serialSetRepository.findByName(serialSet.getName());
    }

    @Override
    public List<SerialSet> getAllSerialSets() {
        return serialSetRepository.findAll();
    }

    @Override
    public SerialSet getSerialSetById(Long id) {
        return serialSetRepository.findById(id)
                .orElseThrow(() -> new SerialSetException(String.format(NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
    }

    @Override
    public boolean deleteSerialSetById(Long id) {
        if (id != null) {
            Optional<SerialSet> optionalSerialSet = serialSetRepository.findById(id);
            if (optionalSerialSet.isPresent()) {
                SerialSet serialSet = optionalSerialSet.get();
                List<SerialNumber> serialNumbers = serialSet.getSerialNumbers();
                serialNumberRepository.deleteAll(serialNumbers);
                serialSetRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    @Async
    @Override
    public CompletableFuture<Void> generateSerialNumbersAsync(SerialSet serialSet) {
        return CompletableFuture.runAsync(() -> generateAndSaveSerialNumbers(serialSet));
    }


    @Override
    public String getCharacterPool(SerialSet serialSet) {
        StringBuilder characters = new StringBuilder();
        if (serialSet.isNumber()) {
            characters.append(NUMERIC_CHARACTERS);
        }
        if (serialSet.isLowerCase()) {
            characters.append(LOWERCASE_CHARACTERS);
        }
        if (serialSet.isUpperCase()) {
            characters.append(UPPERCASE_CHARACTERS);
        }
        return characters.toString();
    }

    @Override
    public String removeExclusions(String input, String exclusions) {
        if (input == null || exclusions == null) {
            return input;
        }
        for (char exclusion : exclusions.toCharArray()) {
            input = input.replace(String.valueOf(exclusion), "");
        }
        return input;
    }

    @Override
    public void generateAndSaveSerialNumbers(SerialSet serialSet) {
        int remainingSerials = serialSet.getQuantity();
        Set<String> uniqueSerials = new HashSet<>();

        while (remainingSerials > 0) {
            int currentBatchSize = Math.min(remainingSerials, batchSize);

            List<SerialNumber> generatedSerials = IntStream.range(0, currentBatchSize)
                    .mapToObj(i -> {
                        String generatedSerial;
                        do {
                            generatedSerial = generateSingleSerial(serialSet);
                        } while (!uniqueSerials.add(generatedSerial));

                        SerialNumber serialNumber = new SerialNumber(generatedSerial);
                        serialNumber.setSerialSet(serialSet);
                        return serialNumber;
                    })
                    .collect(Collectors.toList());

            serialNumberRepository.saveAll(generatedSerials);

            uniqueSerials.clear();

            remainingSerials -= currentBatchSize;
        }
    }

    @Override
    public String generateSingleSerial(SerialSet serialSet) {
        String characters = getCharacterPool(serialSet);
        characters = removeExclusions(characters, serialSet.getExclusions());
        StringBuilder generatedSerial = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < serialSet.getSerialLength(); i++) {
            char randomChar = characters.charAt(random.nextInt(characters.length()));
            generatedSerial.append(randomChar);
        }
        return generatedSerial.toString();
    }

    @Override
    public void validateAndSaveSerialSet(SerialSet serialSet) {
        SerialSet existingSerialSet = serialSetRepository.findByName(serialSet.getName());
        if (existingSerialSet != null) {
            throw new SerialSetException(DUPLICATE_NAME_ERROR_MESSAGE);
        }
        int totalSerials = serialSet.getQuantity();
        if (totalSerials > maxSerialQuantity) {
            throw new SerialSetException(MAX_LIMIT_ERROR_MESSAGE);
        }
        serialSetRepository.save(serialSet);
    }

    @Override
    public void validateSerialSetConfiguration(SerialSet serialSet) {
        if (serialSet.isConfiguration()) {
            if (serialSet.getSerialLength() < minSerialLength || serialSet.getSerialLength() > maxSerialLength) {
                throw new SerialSetException(LENGTH_CONFIG_ERROR_MESSAGE);
            }
        } else {
            if (maxRandomLength == 0) {
                throw new SerialSetException("maxRandomLength should be a non-zero value");
            }
            Random random = new Random();
            serialSet.setSerialLength(random.nextInt(maxRandomLength) + 1);
            serialSet.setNumber(true);
        }
    }
}
