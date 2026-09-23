package com.solarflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.solarflow.model.User;
import com.solarflow.repo.UserRepository;

/**
 * Creates or promotes one operator-configured admin account.
 *
 * The initializer is disabled unless both environment values are supplied.
 * It never changes the password of an existing account.
 */
@Component
public class AdminAccountInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    public AdminAccountInitializer(UserRepository users) {
        this.users = users;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }
        if (adminPassword.length() < 12) {
            throw new IllegalStateException("APP_ADMIN_PASSWORD must be at least 12 characters when admin bootstrap is enabled");
        }

        String email = adminEmail.trim().toLowerCase();
        User user = users.findByEmail(email).orElseGet(() -> {
            User created = new User();
            created.setEmail(email);
            created.setName("Solar Industries Admin");
            created.setPassword(passwordEncoder.encode(adminPassword));
            return created;
        });

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            user.setRole("ADMIN");
            users.save(user);
        } else if (user.getId() == null) {
            users.save(user);
        }
    }
}
