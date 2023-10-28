package com.tracekey.serialnumbergenerator.repository;

import com.tracekey.serialnumbergenerator.entity.SerialNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerialNumberRepository extends JpaRepository<SerialNumber, Long> {

}