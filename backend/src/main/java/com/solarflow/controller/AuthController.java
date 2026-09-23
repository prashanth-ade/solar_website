package com.solarflow.controller;

import com.solarflow.model.User;
import com.solarflow.repo.UserRepository;
import com.solarflow.security.JwtService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    final UserRepository r;
    final JwtService jwt;
    final BCryptPasswordEncoder e = new BCryptPasswordEncoder();

    AuthController(UserRepository r, JwtService j) {
        this.r = r;
        jwt = j;
    }

    @PostMapping("/register")
    AuthResponse register(@RequestBody User x) {
        if (r.findByEmail(x.email).isPresent()) throw new IllegalArgumentException("Email already registered");
        x.role = "CUSTOMER";
        x.password = e.encode(x.password);
        var u = r.save(x);
        return new AuthResponse(jwt.issue(u.email, u.role), u);
    }

    @PostMapping("/login")
    AuthResponse login(@RequestBody User x) {
        var u = r.findByEmail(x.email).filter(v -> e.matches(x.password, v.password)).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        return new AuthResponse(jwt.issue(u.email, u.role), u);
    }

    record AuthResponse(String token, User user) {
    }
}
