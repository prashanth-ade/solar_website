package com.solarflow.model;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "admin_services")
@Getter
@Setter
@NoArgsConstructor
public class SolarService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String description;
    private String imageUrl;
    @Column(nullable = false)
    private String status = "ACTIVE";
    private String icon;
    @Column(name = "display_order", nullable = false)
    private int displayOrder;
    @Column(unique = true)
    private String slug;
    @Column(name = "long_description")
    private String longDescription;
    private String benefits;
}