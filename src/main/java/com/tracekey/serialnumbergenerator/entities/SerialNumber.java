package com.tracekey.serialnumbergenerator.entities;

import javax.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SerialNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;

    @ManyToOne(cascade = CascadeType.ALL)
    private SerialSet serialSet;

    public SerialNumber(String value) {
        this.value = value;
    }
}