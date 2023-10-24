package com.tracekey.serialnumbergenerator.controllers;

import com.tracekey.serialnumbergenerator.entities.SerialSet;
import com.tracekey.serialnumbergenerator.services.ISerialSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/serialsets")
@CrossOrigin(origins = "http://localhost:4200")

public class SerialSetController {

    @Autowired
    private ISerialSetService serialSetService;

    @PostMapping("/create")
    public SerialSet createSerialSet(@RequestBody SerialSet serialSet) {
        return serialSetService.createSerialSet(serialSet);
    }

    @GetMapping("/all")
    public List<SerialSet> getAllSerialSets() {
        return serialSetService.getAllSerialSets();
    }

    @GetMapping("/{id}")
    public SerialSet getSerialSetById(@PathVariable Long id) {
        return serialSetService.getSerialSetById(id);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteSerialSetById(@PathVariable Long id) {
        boolean deleted = serialSetService.deleteSerialSetById(id);
        return deleted ? "Serial Set deleted successfully" : "Serial Set not found";
    }

    @GetMapping("/export/{serialSetName}")
    public String exportSerialNumbersToCSV(@PathVariable String serialSetName) {

            boolean exported = serialSetService.exportSerialNumbersToCSV(serialSetName);
            return exported ? "Serial Numbers exported to CSV successfully" : "Serial Set not found for export";
        }
}
