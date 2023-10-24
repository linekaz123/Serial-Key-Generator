package com.tracekey.serialnumbergenerator.repositories;

import com.tracekey.serialnumbergenerator.entities.SerialNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerialNumberRepository extends JpaRepository<SerialNumber, Long> {

}