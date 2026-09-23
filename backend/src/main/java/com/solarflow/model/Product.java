package com.solarflow.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor public class Product { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; public String name,description,imageUrl; public double price,powerWatts; public boolean active=true; @ManyToOne public Category category; }
