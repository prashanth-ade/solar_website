package com.solarflow.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calculator_requests")
@Getter
@Setter
@NoArgsConstructor
public class CalculatorRequestRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;
    private String name;
    private String email;
    private String phone;
    private String propertyType;
    private double monthlyBill;
    private double monthlyKwh;
    private double roofArea;
    private double recommendedKw;
    private double estimatedCost;
    private double annualSavings;
    private double paybackPeriodYears;
    private LocalDateTime createdAt = LocalDateTime.now();
}
