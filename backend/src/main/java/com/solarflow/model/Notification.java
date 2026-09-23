package com.solarflow.model;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Getter @Setter @NoArgsConstructor public class Notification { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public User user; public String title,message; public boolean readFlag=false; public LocalDateTime createdAt=LocalDateTime.now(); }
