package com.solarflow.model;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Getter @Setter @NoArgsConstructor @Table(name="orders") public class Order { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public User user; public String status="PENDING"; public double total; public LocalDateTime createdAt=LocalDateTime.now(); }
