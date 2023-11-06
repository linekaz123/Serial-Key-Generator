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

    boolean deleteSerialSetByName(String name);

    CompletableFuture<Void> generateSerialNumbersAsync(SerialSet serialSet);

    String getCharacterPool(SerialSet serialSet);

    String removeExclusions(String input, String exclusions);

    void generateSerialNumbers(SerialSet serialSet);

    boolean validateSerialSet(SerialSet serialSet);
    SerialSet saveSerialSet(SerialSet serialSet);

    String generateSingleSerial(SerialSet serialSet,String characters);

    boolean validateSerialSetConfiguration(SerialSet serialSet);

}
