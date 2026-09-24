package com.solarflow.controller;

import com.solarflow.model.*;
import com.solarflow.repo.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminPersistenceController {
    private static final Set<String> SERVICE_STATUSES = Set.of("ACTIVE", "INACTIVE");
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;

    private final SolarServiceRepository services;
    private final CalculatorRequestRecordRepository requests;
    private final CompanySettingsRepository settings;
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

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

    @PostMapping("/services/upload-image")
    public Map<String, String> uploadServiceImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String contentType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid image type. Allowed: JPG, PNG, WEBP");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File too large. Maximum size is 5MB");
        }
        try {
            String extension = getExtension(contentType);
            String storedName = "service-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Path serviceDir = Paths.get(uploadDir, "services");
            Files.createDirectories(serviceDir);
            Path target = serviceDir.resolve(storedName);
            Files.copy(file.getInputStream(), target);
            String imageUrl = "/uploads/services/" + storedName;
            return Map.of("imageUrl", imageUrl, "originalName", file.getOriginalFilename());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to store image: " + e.getMessage());
        }
    }

    private String getExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
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