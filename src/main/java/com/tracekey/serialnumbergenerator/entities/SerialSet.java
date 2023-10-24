package com.tracekey.serialnumbergenerator.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
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
    private Long id;

    @Column(unique = true)
    private String name;

    private int quantity;

    private Date createdOn;

    private boolean configuration;
    private int serialLength;
    private boolean numbers;
    private boolean upperCase;
    private boolean lowerCase;
    private String exclusions;

    @OneToMany(mappedBy = "serialSet")
    private List<SerialNumber> serialNumbers;


}