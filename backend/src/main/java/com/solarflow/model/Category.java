package com.solarflow.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor public class Category { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(unique=true) public String name; public String description; }
