package com.solarflow.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="users") @Getter @Setter @NoArgsConstructor public class User { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(unique=true) public String email; @JsonProperty(access=JsonProperty.Access.WRITE_ONLY) public String password; public String name,role="CUSTOMER"; }
