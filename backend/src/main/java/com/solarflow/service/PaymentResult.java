package com.solarflow.service;

public record PaymentResult(String reference, String status, String provider, String message) {
}
