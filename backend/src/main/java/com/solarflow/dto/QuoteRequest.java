package com.solarflow.dto;

import jakarta.validation.constraints.*;

public record QuoteRequest(
        @DecimalMin(value = "0.1", message = "monthlyKwh must be greater than zero")
        Double monthlyKwh,
        @DecimalMin(value = "0.1", message = "roofArea must be greater than zero")
        Double roofArea,
        @DecimalMin(value = "0", message = "estimatedMonthlyGeneration cannot be negative")
        Double estimatedMonthlyGeneration,
        @DecimalMin(value = "0", message = "monthlySavings cannot be negative")
        Double monthlySavings,
        @DecimalMin(value = "0", message = "paybackPeriodYears cannot be negative")
        Double paybackPeriodYears,
        @DecimalMin(value = "0", message = "twentyFiveYearSavings cannot be negative")
        Double twentyFiveYearSavings,
        @Pattern(regexp = "^\\d{6}$", message = "pincode must be a six digit postal code")
        String pincode,
        @Size(max = 50, message = "propertyType is too long")
        String propertyType,
        @Size(max = 100, message = "name is too long") String name,
        @Email(message = "email must be valid") @Size(max = 255) String email,
        @Pattern(regexp = "^[0-9+()\\- ]{7,25}$", message = "phone must be valid") String phone,
        @Size(max = 100, message = "city is too long") String city,
        @Size(max = 2000, message = "message is too long") String message) {
}