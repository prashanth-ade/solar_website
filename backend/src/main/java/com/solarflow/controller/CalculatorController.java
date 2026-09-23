package com.solarflow.controller;

import com.solarflow.dto.CalculatorRequest;
import com.solarflow.model.CalculatorRequestRecord;
import com.solarflow.model.User;
import com.solarflow.repo.CalculatorRequestRecordRepository;
import com.solarflow.repo.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {
    private final CalculatorRequestRecordRepository requests;
    private final UserRepository users;

    public CalculatorController(CalculatorRequestRecordRepository requests, UserRepository users) {
        this.requests = requests;
        this.users = users;
    }

    @PostMapping
    public Map<String, Double> calculate(@Valid @RequestBody CalculatorRequest request, Authentication authentication) {
        double monthlyKwh = request.monthlyKwh() == null ? 300 : request.monthlyKwh();
        double roofArea = request.roofArea() == null ? 20 : request.roofArea();
        double kw = Math.max(1, Math.min(roofArea / 8, monthlyKwh / 120));
        double cost = Math.round(kw * 1200 * 100) / 100d;
        double annualSavings = Math.round(kw * 120 * 12 * .18 * 100) / 100d;
        double monthlyGeneration = Math.round(kw * 120 * 100) / 100d;
        double monthlySavings = Math.round(annualSavings / 12 * 100) / 100d;
        double payback = annualSavings == 0 ? 0 : Math.round(cost / annualSavings * 100) / 100d;
        double twentyFiveYearSavings = Math.round(annualSavings * 25 * 100) / 100d;
        Map<String, Double> result = new LinkedHashMap<>();
        result.put("estimatedKw", round(kw));
        result.put("estimatedCost", cost);
        result.put("annualSavings", annualSavings);
        result.put("estimatedMonthlyGeneration", monthlyGeneration);
        result.put("monthlyGeneration", monthlyGeneration);
        result.put("monthlySavings", monthlySavings);
        result.put("estimatedMonthlySavings", monthlySavings);
        result.put("paybackPeriodYears", payback);
        result.put("paybackPeriod", payback);
        result.put("twentyFiveYearSavings", twentyFiveYearSavings);
        CalculatorRequestRecord record = new CalculatorRequestRecord();
        record.setUser(resolveUser(authentication));
        record.setName(request.name());
        record.setEmail(request.email());
        record.setPhone(request.phone());
        record.setPropertyType(request.propertyType());
        record.setMonthlyBill(monthlyKwh * 8);
        record.setMonthlyKwh(monthlyKwh);
        record.setRoofArea(roofArea);
        record.setRecommendedKw(kw);
        record.setEstimatedCost(cost);
        record.setAnnualSavings(annualSavings);
        record.setPaybackPeriodYears(payback);
        requests.save(record);
        return result;
    }

    private User resolveUser(Authentication authentication) {
        if (authentication == null) return null;
        return users.findByEmail(authentication.getName()).orElse(null);
    }

    private double round(double value) {
        return Math.round(value * 100) / 100d;
    }
}
