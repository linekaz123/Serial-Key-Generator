package com.tracekey.serialnumbergenerator.controller;

import com.tracekey.serialnumbergenerator.entity.SerialSet;
import com.tracekey.serialnumbergenerator.service.ICsvExportService;
import com.tracekey.serialnumbergenerator.service.ISerialSetService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling SerialSet-related operations.
 */
@RestController
@RequestMapping("/api/serialsets")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class SerialSetController {

    private final ISerialSetService serialSetService;
    private final ICsvExportService csvExportService;

    /**
     * Endpoint to create a new SerialSet with serial numbers.
     *
     * @param serialSet The SerialSet to be created.
     * @return The created SerialSet.
     */
    @PostMapping("/create")
    public SerialSet createSerialSet(@RequestBody SerialSet serialSet) {
        return serialSetService.createSerialSet(serialSet);
    }

    /**
     * Endpoint to retrieve all SerialSets.
     *
     * @return List of all SerialSets.
     */
    @GetMapping("/all")
    public List<SerialSet> getAllSerialSets() {
        return serialSetService.getAllSerialSets();
    }

    /**
     * Endpoint to retrieve a SerialSet by its ID.
     *
     * @param id The ID of the SerialSet to retrieve.
     * @return The retrieved SerialSet.
     */
    @GetMapping("/{id}")
    public SerialSet getSerialSetById(@PathVariable Long id) {
        return serialSetService.getSerialSetById(id);
    }

    /**
     * Endpoint to delete a SerialSet by its ID.
     *
     * @param id The ID of the SerialSet to delete.
     * @return True if the deletion is successful, false otherwise.
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteSerialSetById(@PathVariable Long id) {
        return serialSetService.deleteSerialSetById(id);
    }

    /**
     * Endpoint to export serial numbers of a specified SerialSet to CSV.
     *
     * @param serialSetName The name of the SerialSet to export.
     * @return True if the export is successful, false otherwise.
     */
    @GetMapping("/export/{serialSetName}")
    public boolean exportSerialNumbersToCSV(@PathVariable String serialSetName) {
        return csvExportService.exportSerialNumbersToCSV(serialSetName);
    }
}
