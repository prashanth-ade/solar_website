package com.solarflow.controller;

import com.solarflow.model.CompanySettings;
import com.solarflow.repo.CompanySettingsRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {
    private final CompanySettingsRepository settings;

    public SettingsController(CompanySettingsRepository settings) {
        this.settings = settings;
    }

    @GetMapping
    public CompanySettings get() {
        return settings.findById(1L).orElseGet(() -> settings.save(defaults()));
    }

    private CompanySettings defaults() {
        CompanySettings value = new CompanySettings();
        value.setId(1L);
        value.setCompanyName("Solar Industries");
        return value;
    }
}