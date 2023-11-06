package com.tracekey.serialnumbergenerator.service;

import com.tracekey.serialnumbergenerator.entity.SerialSet;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ISerialSetService {

    SerialSet createSerialSet(SerialSet serialSet);

    List<SerialSet> getAllSerialSets();

    SerialSet getSerialSetById(Long id);

    boolean deleteSerialSetById(Long id);

    CompletableFuture<Void> generateSerialNumbersAsync(SerialSet serialSet);

    String getCharacterPool(SerialSet serialSet);

    String removeExclusions(String input, String exclusions);

    void generateSerialNumbers(SerialSet serialSet);

    boolean validateSerialSet(SerialSet serialSet);
    SerialSet SaveSerialSet(SerialSet serialSet);

    String generateSingleSerial(SerialSet serialSet,String characters);

    boolean validateSerialSetConfiguration(SerialSet serialSet);

}
