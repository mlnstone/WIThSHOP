package com.example.backend.point.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "point_signup_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointSignupConfig {
    @Id
    private Long id; // 항상 1
    @Column(nullable = false)
    private Long amount;
}