package com.tracekey.serialnumbergenerator.services;

import com.tracekey.serialnumbergenerator.entities.SerialNumber;
import com.tracekey.serialnumbergenerator.entities.SerialSet;
import com.tracekey.serialnumbergenerator.exceptions.CsvExportCriteriaException;
import com.tracekey.serialnumbergenerator.exceptions.CsvExportException;
import com.tracekey.serialnumbergenerator.exceptions.DuplicateSerialSetNameException;
import com.tracekey.serialnumbergenerator.exceptions.InvalidSerialSetException;
import com.tracekey.serialnumbergenerator.repositories.SerialNumberRepository;
import com.tracekey.serialnumbergenerator.repositories.SerialSetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class ISerialSetServiceImpl implements ISerialSetService {
    @Autowired
    private SerialSetRepository serialSetRepository;
    @Autowired
    private SerialNumberRepository serialNumberRepository;
    @Autowired
    private ICsvExportService iCsvExportService;
    private final Random random = new Random();

    @Override
    public SerialSet createSerialSet(SerialSet serialSet) {
        // Validate length configuration
        if (serialSet.isConfiguration()) {

            if (serialSet.getSerialLength() < 10 || serialSet.getSerialLength() > 20 ) {

                throw new InvalidSerialSetException("Invalid length configuration");
            }}
        //Set default configuration
        else{
            int randomSerialLength = random.nextInt(12);
            serialSet.setSerialLength(randomSerialLength);
            serialSet.setNumbers(true);
        }
        serialSet.setCreatedOn(new Date());

        // Validate name configuration
            SerialSet existingSerialSet = serialSetRepository.findByName(serialSet.getName());
            if (existingSerialSet != null) {
                // Serial set with the same name already exists
                throw new DuplicateSerialSetNameException("Serial set with the same name already exists");
            }

            // Check if the total quantity of serial numbers exceeds the limit
            int totalSerials = serialSet.getQuantity();
            if (totalSerials > 10000) {
                throw new InvalidSerialSetException("Request exceeds the maximum limit of serial numbers");
            }
     generateSerialNumbers(serialSet);





            return serialSetRepository.findByName(serialSet.getName());
        }


    @Override
    public List<SerialSet> getAllSerialSets() {
        return serialSetRepository.findAll();
    }

    @Override
    public SerialSet getSerialSetById(Long id) {
       return serialSetRepository.findById(id).orElse(null);

    }

    @Override
    public boolean deleteSerialSetById(Long id) {
        SerialSet serialSet=serialSetRepository.findById(id).orElse(null);
        if(serialSet!=null){
            serialSetRepository.deleteById(id);
        return true;
        }
        return false;
    }

    public List<SerialNumber> generateSerialNumbers(SerialSet serialSet) {
        List<SerialNumber> generatedSerials = new ArrayList<>();

        for (int i = 0; i < serialSet.getQuantity(); i++) {
            String generatedValue = generateSingleSerial(serialSet);
            SerialNumber serialNumber = new SerialNumber(generatedValue);

            // Check uniqueness
            while (containsSerialNumber(generatedSerials, serialNumber)) {
                generatedValue = generateSingleSerial(serialSet);
                serialNumber.setValue(generatedValue);

            }
            serialNumber.setSerialSet(serialSet);
            generatedSerials.add(serialNumber);
            serialNumberRepository.save(serialNumber);

        }

        return generatedSerials;
    }

    private boolean containsSerialNumber(List<SerialNumber> serialNumbers, SerialNumber serialNumber) {
        for (SerialNumber existingSerial : serialNumbers) {
            if (existingSerial.getValue().equals(serialNumber.getValue())) {
                return true;
            }
        }
        return false;
    }



    @Override
   public String generateSingleSerial(SerialSet serialSet) {
        StringBuilder serialNumberBuilder = new StringBuilder();
        String characters = "";

        if (serialSet.isNumbers()) {
            characters = "0123456789";
        }
            if (serialSet.isLowerCase()) {
                characters += "abcdefghijklmnopqrstuvwxyz";
            }
            if (serialSet.isUpperCase()) {
                characters += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            }



        characters = removeExclusions(characters, serialSet.getExclusions());

        int length = serialSet.getSerialLength();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            serialNumberBuilder.append(characters.charAt(randomIndex));
        }

        return serialNumberBuilder.toString();
    }
    @Override
    public String removeExclusions(String input, String exclusions) {
        if (input == null || exclusions == null) {
            return input;
        }

        for (char exclusion : exclusions.toCharArray()) {
            if (input != null) {
                input = input.replace(String.valueOf(exclusion), "");
            }
        }
        return input;
    }

    @Override
    public boolean exportSerialNumbersToCSV(String serialSetName) {
        SerialSet serialSet = serialSetRepository.findByName(serialSetName);

        if (serialSet == null) {
            throw new CsvExportCriteriaException("Serial set not found for export");
        }

        if (serialSet.getSerialNumbers() == null || serialSet.getSerialNumbers().isEmpty()) {
            throw new CsvExportCriteriaException("No serial numbers created for this serial set");
        }
        if (serialSet.getSerialNumbers().size()<serialSet.getQuantity()) {
            throw new  CsvExportCriteriaException("Serial set numbers generation is incomplete");
        }

        try {
            iCsvExportService.exportToCsv(serialSet);
            return true;
        } catch (CsvExportException e) {
            throw new CsvExportException("Error exporting serial numbers to CSV", e);
        }
    }
}
