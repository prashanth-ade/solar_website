package com.solarflow.repo;

import com.solarflow.model.SolarService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolarServiceRepository extends JpaRepository<SolarService, Long> {
    List<SolarService> findAllByOrderByDisplayOrderAscNameAsc();
}
