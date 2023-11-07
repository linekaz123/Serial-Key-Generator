package com.tracekey.serialnumbergenerator.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@Getter
@Setter
public class SerialSetRequest implements Serializable {

    private static final long serialSetRequestVersionUID = 1L;

    private String name;

    private int quantity;

    private LocalDateTime createdDate;

    private int serialLength;

    private boolean configuration;

    private boolean number;

    private boolean lowerCase;

    private boolean upperCase;

    private String exclusions = "";
}
