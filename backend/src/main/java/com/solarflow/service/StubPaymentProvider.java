package com.solarflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StubPaymentProvider implements PaymentProvider {
    @Value("${app.payment.provider:stub}")
    private String provider;

    @Override
    public PaymentResult initiate(String reference, double amount, String currency, String customerEmail) {
        return new PaymentResult(reference, "INITIATED", providerName(), "Payment initiated successfully.");
    }

    @Override
    public PaymentResult verify(String reference) {
        return new PaymentResult(reference, "PAID", providerName(), "Verification passed via configured payment provider.");
    }

    @Override
    public String providerName() {
        return provider;
    }
}
