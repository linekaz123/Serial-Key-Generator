package com.tracekey.serialnumbergenerator.services;

import com.tracekey.serialnumbergenerator.entities.SerialNumber;
import com.tracekey.serialnumbergenerator.entities.SerialSet;

import java.util.List;

public interface ISerialSetService {
    SerialSet createSerialSet(SerialSet serialSet);

    List<SerialSet> getAllSerialSets();

    SerialSet getSerialSetById(Long id);

    boolean deleteSerialSetById(Long id);
    List<SerialNumber> generateSerialNumbers(SerialSet serialSet);
   String generateSingleSerial(SerialSet serialSet);
  String removeExclusions(String input, String exclusions);

    boolean exportSerialNumbersToCSV(String serialSetName);
}