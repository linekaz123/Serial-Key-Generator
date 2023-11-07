package com.tracekey.serialnumbergenerator.service;

import com.tracekey.serialnumbergenerator.entity.SerialSet;
import com.tracekey.serialnumbergenerator.dto.SerialSetRequest;
import com.tracekey.serialnumbergenerator.dto.SerialSetResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ISerialSetService {

    SerialSetResponse createSerialSet(SerialSetRequest serialSet);

    List<SerialSetResponse> getAllSerialSets();

    SerialSetResponse getSerialSetByName(String name);

    void deleteSerialSetByName(String name);

    CompletableFuture<Void> generateSerialNumbersAsync(SerialSet serialSet);

    String getCharacterPool(SerialSet serialSet);

    String removeExclusions(String input, String exclusions);

    void generateSerialNumbers(SerialSet serialSet);

    void validateSerialSet(SerialSet serialSet);

    void saveSerialSet(SerialSet serialSet);

    String generateSingleSerial(SerialSet serialSet,String characters);

    void validateSerialSetConfiguration(SerialSet serialSet);

}
