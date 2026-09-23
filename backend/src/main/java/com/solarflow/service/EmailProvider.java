package com.solarflow.service;

public interface EmailProvider {
    void send(String to, String subject, String body);
}
