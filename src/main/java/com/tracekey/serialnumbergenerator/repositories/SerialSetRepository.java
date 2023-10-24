package com.tracekey.serialnumbergenerator.repositories;

import com.tracekey.serialnumbergenerator.entities.SerialSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SerialSetRepository extends JpaRepository<SerialSet, Long> {
    SerialSet findByName(String name);
}
