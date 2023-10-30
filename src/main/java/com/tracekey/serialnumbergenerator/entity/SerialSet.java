package com.tracekey.serialnumbergenerator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SerialSet {

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
