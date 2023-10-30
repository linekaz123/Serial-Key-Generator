package com.tracekey.serialnumbergenerator.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SerialNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String value;

    private LocalDateTime createdDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "serial_set_id")
    private SerialSet serialSet;

    public SerialNumber(String value) {
        this.value = value;
    }

    @PrePersist
    protected void onCreate() {
        // Automatic initialization before persisting
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }
}
