package com.solarflow.controller;

import com.solarflow.dto.QuoteRequest;
import com.solarflow.model.*;
import com.solarflow.repo.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {
    private final QuoteRepository quotes;
    private final UserRepository users;
    public QuoteController(QuoteRepository quotes, UserRepository users) {
        this.quotes = quotes; this.users = users;
    }

    @GetMapping
    public List<Quote> mine(Authentication authentication) {
        return quotes.findByUserOrderByCreatedAtDesc(user(authentication));
    }

    @PostMapping
    public Quote create(@Valid @RequestBody QuoteRequest request, Authentication authentication) {
        Quote quote = new Quote();
        quote.user = authentication == null ? null : user(authentication);
        quote.monthlyKwh = value(request.monthlyKwh());
        quote.roofArea = value(request.roofArea());
        quote.estimatedMonthlyGeneration = value(request.estimatedMonthlyGeneration());
        quote.monthlySavings = value(request.monthlySavings());
        quote.paybackPeriodYears = value(request.paybackPeriodYears());
        quote.twentyFiveYearSavings = value(request.twentyFiveYearSavings());
        quote.pincode = request.pincode(); quote.propertyType = request.propertyType();
        quote.name = request.name(); quote.email = request.email(); quote.phone = request.phone();
        quote.city = request.city(); quote.message = request.message();
        return quotes.save(quote);
    }

    @GetMapping("/admin/summary")
    public Map<String, Long> summary() {
        return Map.of("totalQuotes", quotes.count(), "pendingQuotes", quotes.countByStatus("DRAFT"));
    }

    @GetMapping("/admin")
    public List<Quote> allForAdmin() {
        return quotes.findAll().stream()
                .sorted(Comparator.comparing(quote -> quote.createdAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @PatchMapping("/admin/{id}/status")
    public Quote updateStatus(@PathVariable Long id, @RequestParam String status) {
        if (!Set.of("DRAFT", "PENDING", "CONTACTED", "APPROVED", "REJECTED", "COMPLETED").contains(status))
            throw new IllegalArgumentException("Invalid quote status");
        Quote quote = quotes.findById(id).orElseThrow(() -> new IllegalArgumentException("Quote not found"));
        quote.status = status;
        return quotes.save(quote);
    }

    private User user(Authentication authentication) {
        return users.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
    }
    private double value(Double value) { return value == null ? 0 : value; }
}