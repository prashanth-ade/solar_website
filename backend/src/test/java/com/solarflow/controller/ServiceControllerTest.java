package com.solarflow.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.solarflow.model.SolarService;
import com.solarflow.repo.SolarServiceRepository;

public class ServiceControllerTest {

    @Test
    void publicCatalogOnlyReturnsActiveServices() {
        SolarServiceRepository repository = mock(SolarServiceRepository.class);
        SolarService active = service("solar-panels", "ACTIVE");
        SolarService hidden = service("legacy-offer", "INACTIVE");
        when(repository.findAllByOrderByDisplayOrderAscNameAsc()).thenReturn(List.of(hidden, active));

        ServiceController controller = new ServiceController(repository);
        List<SolarService> result = controller.publicServices();

        assertEquals(1, result.size());
        assertEquals("solar-panels", result.get(0).getSlug());
    }

    @Test
    void publicCatalogResolvesActiveServiceBySlug() {
        SolarServiceRepository repository = mock(SolarServiceRepository.class);
        when(repository.findAllByOrderByDisplayOrderAscNameAsc()).thenReturn(List.of(service("solar-panels", "ACTIVE")));

        ServiceController controller = new ServiceController(repository);
        assertEquals("solar-panels", controller.bySlug("solar-panels").getSlug());
    }

    private SolarService service(String slug, String status) {
        SolarService value = new SolarService();
        value.setName("Solar " + slug);
        value.setSlug(slug);
        value.setStatus(status);
        return value;
    }
}