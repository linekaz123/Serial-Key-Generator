package com.tracekey.serialnumbergenerator.controller;

import com.tracekey.serialnumbergenerator.entity.SerialSet;
import com.tracekey.serialnumbergenerator.service.ICsvExportService;
import com.tracekey.serialnumbergenerator.service.ISerialSetService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/serialsets")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class SerialSetController {

    private final ISerialSetService serialSetService;
    private final ICsvExportService csvExportService;

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
    public boolean deleteSerialSetById(@PathVariable Long id) {
        return serialSetService.deleteSerialSetById(id);
    }

    @GetMapping("/export/{serialSetName}")
    public boolean exportSerialNumbersToCSV(@PathVariable String serialSetName) {
        return csvExportService.exportSerialNumbersToCSV(serialSetName);
    }
}
