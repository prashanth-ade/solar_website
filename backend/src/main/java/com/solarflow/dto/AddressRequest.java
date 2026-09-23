package com.solarflow.dto;

import jakarta.validation.constraints.*;

public record AddressRequest(
        @Size(max = 100) String label,
        @NotBlank @Size(max = 255) String line1,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 100) String state,
        @NotBlank @Size(max = 30) String postalCode,
        @NotBlank @Size(max = 100) String country) {
}
