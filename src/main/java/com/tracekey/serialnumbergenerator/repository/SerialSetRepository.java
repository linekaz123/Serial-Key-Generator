package com.tracekey.serialnumbergenerator.repository;

import com.tracekey.serialnumbergenerator.entity.SerialSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SerialSetRepository extends JpaRepository<SerialSet, Long> {
    Optional<SerialSet> findByName(String name);
}
