package com.solarflow.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor public class Review { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public User user; @ManyToOne(optional=false) public Product product; public int rating; public String comment; }
