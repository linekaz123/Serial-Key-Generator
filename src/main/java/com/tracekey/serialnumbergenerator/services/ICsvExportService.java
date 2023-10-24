package com.tracekey.serialnumbergenerator.services;

import com.tracekey.serialnumbergenerator.entities.SerialNumber;
import com.tracekey.serialnumbergenerator.entities.SerialSet;

import java.util.List;

public interface ICsvExportService {
    public void exportToCsv(SerialSet serialSet);
}
