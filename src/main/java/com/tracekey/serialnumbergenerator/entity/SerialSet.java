package com.tracekey.serialnumbergenerator.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "serial_set")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SerialSet implements Serializable {

    private static final long serialSetVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;

    private int quantity;

    private LocalDateTime createdDate;

    private int serialLength;

    private boolean configuration;

    private boolean number;

    private boolean lowerCase;

    private boolean upperCase;

    private String exclusions = "";

    public SerialSet setName(String name) {
        this.name = name;
        return this;
    }

    public SerialSet setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public SerialSet setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public SerialSet setSerialLength(int serialLength) {
        this.serialLength = serialLength;
        return this;
    }

    public SerialSet setConfiguration(boolean configuration) {
        this.configuration = configuration;
        return this;
    }

    public SerialSet setNumber(boolean number) {
        this.number = number;
        return this;
    }

    public SerialSet setLowerCase(boolean lowerCase) {
        this.lowerCase = lowerCase;
        return this;
    }

    public SerialSet setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
        return this;
    }

    public SerialSet setExclusions(String exclusions) {
        this.exclusions = exclusions;
        return this;
    }

    public SerialSet setSerialNumbers(List<SerialNumber> serialNumbers) {
        this.serialNumbers = serialNumbers;
        return this;
    }

    @OneToMany(mappedBy = "serialSet", fetch=FetchType.LAZY)
    @JsonManagedReference
    private List<SerialNumber> serialNumbers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        // Automatic initialization before persisting
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }
}
