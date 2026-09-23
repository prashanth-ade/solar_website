package com.solarflow.dto;

import jakarta.validation.constraints.*;

public record CalculatorRequest(
        @DecimalMin(value = "0.1", message = "monthlyKwh must be greater than zero")
        Double monthlyKwh,
        @DecimalMin(value = "0.1", message = "roofArea must be greater than zero")
        Double roofArea,
        @Pattern(regexp = "^\\d{6}$", message = "pincode must be a six digit postal code")
        String pincode,
        @Size(max = 50, message = "propertyType is too long")
        String propertyType,
        @Size(max = 255, message = "name is too long")
        String name,
        @Email(message = "email must be valid")
        String email,
        @Size(max = 50, message = "phone is too long")
        String phone) {
}
