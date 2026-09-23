package com.solarflow.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_settings")
@Getter
@Setter
@NoArgsConstructor
public class CompanySettings {
    @Id
    private Long id = 1L;
    @Column(nullable = false)
    private String companyName;
    private String phone;
    private String email;
    private String address;
}
