package com.solarflow.repo;

import com.solarflow.model.CalculatorRequestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CalculatorRequestRecordRepository extends JpaRepository<CalculatorRequestRecord, Long> {
    List<CalculatorRequestRecord> findAllByOrderByCreatedAtDesc();
}
