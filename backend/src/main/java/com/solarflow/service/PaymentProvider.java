package com.solarflow.service;

public interface PaymentProvider {
    PaymentResult initiate(String reference, double amount, String currency, String customerEmail);
    PaymentResult verify(String reference);

    default String providerName() {
        return getClass().getSimpleName();
    }
}
