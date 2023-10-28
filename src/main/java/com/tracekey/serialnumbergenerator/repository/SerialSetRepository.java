package com.tracekey.serialnumbergenerator.repository;

import com.tracekey.serialnumbergenerator.entity.SerialSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerialSetRepository extends JpaRepository<SerialSet, Long> {
    SerialSet findByName(String name);
}
