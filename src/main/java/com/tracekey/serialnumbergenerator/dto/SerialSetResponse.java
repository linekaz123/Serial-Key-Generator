package com.tracekey.serialnumbergenerator.dto;

import com.tracekey.serialnumbergenerator.entity.SerialNumber;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Getter
@Setter
public class SerialSetResponse {
    private String name;
    private int quantity;
    private LocalDateTime createdDate;

    private List<SerialNumber> serialNumberResponseList;

}
