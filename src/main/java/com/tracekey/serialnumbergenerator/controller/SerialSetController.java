package com.tracekey.serialnumbergenerator.controller;

import com.tracekey.serialnumbergenerator.dto.SerialSetRequest;
import com.tracekey.serialnumbergenerator.dto.SerialSetResponse;
import com.tracekey.serialnumbergenerator.service.ICsvExportService;
import com.tracekey.serialnumbergenerator.service.ISerialSetService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling SerialSet-related operations.
 */
@RestController
@RequestMapping("/api/serialsets")
@CrossOrigin(origins = "http://localhost:4200")
public class SerialSetController {

    private final ISerialSetService serialSetService;
    private final ICsvExportService csvExportService;

    public SerialSetController(ISerialSetService serialSetService, ICsvExportService csvExportService) {
        this.serialSetService = serialSetService;
        this.csvExportService = csvExportService;
    }

    /**
     * Endpoint to create a new SerialSet with serial numbers.
     *
     * @param serialSetRequest The SerialSetRequest containing the information for the new SerialSet.
     * @return The created SerialSet.
     */
    @PostMapping("/create")
    public SerialSetResponse createSerialSet(@RequestBody SerialSetRequest serialSetRequest) {
        return serialSetService.createSerialSet(serialSetRequest);
    }

    /**
     * Endpoint to retrieve all SerialSets.
     *
     * @return List of all SerialSets.
     */
    @GetMapping("/all")
    public List<SerialSetResponse> getAllSerialSets() {
        return serialSetService.getAllSerialSets();
    }

    /**
     * Endpoint to retrieve a SerialSet by its name.
     *
     * @param name The name of the SerialSet to retrieve.
     * @return The retrieved SerialSet.
     */
    @GetMapping("/{name}")
    public SerialSetResponse getSerialSetByName(@PathVariable String name) {
        return serialSetService.getSerialSetByName(name);
    }

    /**
     * Endpoint to delete a SerialSet by its name.
     *
     * @param name The name of the SerialSet to delete.
     *
     */
    @DeleteMapping("/delete/{name}")
    public void deleteSerialSetByName(@PathVariable String name) {
        serialSetService.deleteSerialSetByName(name);
    }

    /**
     * Endpoint to export serial numbers of a specified SerialSet to CSV.
     *
     * @param serialSetName The name of the SerialSet to export.
     *
     */
    @GetMapping("/export/{serialSetName}")
    public void exportSerialNumbersToCSV(@PathVariable String serialSetName) {
         csvExportService.exportSerialNumbersToCSV(serialSetName);
    }
}
