package com.solarflow.controller;

import com.solarflow.model.SolarService;
import com.solarflow.repo.SolarServiceRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final SolarServiceRepository services;

    public ServiceController(SolarServiceRepository services) {
        this.services = services;
    }

    @GetMapping
    public List<SolarService> publicServices() {
        return services.findAllByOrderByDisplayOrderAscNameAsc().stream()
                .filter(service -> "ACTIVE".equalsIgnoreCase(service.getStatus()))
                .toList();
    }

    @GetMapping("/{slug}")
    public SolarService bySlug(@PathVariable String slug) {
        return services.findAllByOrderByDisplayOrderAscNameAsc().stream()
                .filter(service -> slug.equalsIgnoreCase(service.getSlug())
                        && "ACTIVE".equalsIgnoreCase(service.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
    }
}