package com.tracekey.serialnumbergenerator.dto;

import com.tracekey.serialnumbergenerator.entity.SerialNumber;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
@Data
@Getter
@Setter
public class SerialSetResponse implements Serializable {

    private static final long serialSetResponseVersionUID = 1L;
    private String name;
    private int quantity;
    private LocalDateTime createdDate;

    private List<SerialNumber> serialNumberResponseList;

}
