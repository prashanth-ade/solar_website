package com.solarflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsoleEmailProvider implements EmailProvider {
    @Value("${app.email.provider:console}")
    private String provider;

    @Override
    public void send(String to, String subject, String body) {
        if ("console".equalsIgnoreCase(provider)) {
            System.out.printf("EMAIL :: to=%s subject=%s body=%s%n", to, subject, body);
            return;
        }

        System.out.printf("EMAIL provider '%s' configured; queued for %s%n", provider, to);
    }
}
