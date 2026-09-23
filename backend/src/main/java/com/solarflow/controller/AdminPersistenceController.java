package com.solarflow.controller;

import com.solarflow.model.*;
import com.solarflow.repo.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminPersistenceController {
    private static final Set<String> SERVICE_STATUSES = Set.of("ACTIVE", "INACTIVE");
    private final SolarServiceRepository services;
    private final CalculatorRequestRecordRepository requests;
    private final CompanySettingsRepository settings;

    public AdminPersistenceController(SolarServiceRepository services,
                                      CalculatorRequestRecordRepository requests,
                                      CompanySettingsRepository settings) {
        this.services = services;
        this.requests = requests;
        this.settings = settings;
    }

    @GetMapping("/services")
    public List<SolarService> listServices() {
        return services.findAllByOrderByDisplayOrderAscNameAsc();
    }

    @GetMapping("/services/{id}")
    public SolarService getService(@PathVariable Long id) {
        return services.findById(id).orElseThrow(() -> new IllegalArgumentException("Service not found"));
    }

    @PostMapping("/services")
    public SolarService createService(@RequestBody SolarService service) {
        service.setId(null);
        return services.save(normalizeService(service));
    }

    @PutMapping("/services/{id}")
    public SolarService updateService(@PathVariable Long id, @RequestBody SolarService input) {
        SolarService service = services.findById(id).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        service.setName(input.getName());
        service.setDescription(input.getDescription());
        service.setImageUrl(input.getImageUrl());
        service.setIcon(input.getIcon());
        service.setDisplayOrder(input.getDisplayOrder());
        service.setStatus(input.getStatus());
        service.setSlug(input.getSlug());
        service.setLongDescription(input.getLongDescription());
        service.setBenefits(input.getBenefits());
        return services.save(normalizeService(service));
    }

    @DeleteMapping("/services/{id}")
    public void deleteService(@PathVariable Long id) {
        if (!services.existsById(id)) throw new IllegalArgumentException("Service not found");
        services.deleteById(id);
    }

    @GetMapping("/calculator-requests")
    public List<CalculatorRequestRecord> calculatorRequests() {
        return requests.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/settings")
    public CompanySettings getSettings() {
        return settings.findById(1L).orElseGet(() -> settings.save(defaultSettings()));
    }

    @PutMapping("/settings")
    public CompanySettings updateSettings(@RequestBody CompanySettings input) {
        CompanySettings current = getSettings();
        current.setCompanyName(input.getCompanyName());
        current.setPhone(input.getPhone());
        current.setEmail(input.getEmail());
        current.setAddress(input.getAddress());
        return settings.save(current);
    }

    private SolarService normalizeService(SolarService service) {
        if (service.getName() == null || service.getName().isBlank()) {
            throw new IllegalArgumentException("Service name is required");
        }
        String status = service.getStatus() == null ? "ACTIVE" : service.getStatus().toUpperCase(Locale.ROOT);
        if (!SERVICE_STATUSES.contains(status)) throw new IllegalArgumentException("Invalid service status");
        service.setStatus(status);
        String rawSlug = service.getSlug() == null || service.getSlug().isBlank() ? service.getName() : service.getSlug();
        String slug = slugify(rawSlug);
        service.setSlug(slug.isEmpty() ? null : slug);
        if (service.getBenefits() != null) {
            service.setBenefits(service.getBenefits().trim());
        }
        return service;
    }

    private String slugify(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
    }

    private CompanySettings defaultSettings() {
        CompanySettings value = new CompanySettings();
        value.setId(1L);
        value.setCompanyName("Solar Industries");
        return value;
    }
}