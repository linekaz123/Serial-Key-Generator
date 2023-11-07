package com.tracekey.serialnumbergenerator.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "serial_number")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SerialNumber implements Serializable {

    private static final long serialNumberVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String value;

    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "serial_set_id")
    private SerialSet serialSet;

    public SerialNumber setValue(String value) {
        this.value = value;
        return this;
    }

    public SerialNumber setSerialSet(SerialSet serialSet) {
        this.serialSet = serialSet;
        return this;
    }
    public SerialNumber(String value, SerialSet serialSet) {
        this.value = value;
        this.serialSet = serialSet;
    }

    @PrePersist
    protected void onCreate() {
        // Automatic initialization before persisting
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }
}
