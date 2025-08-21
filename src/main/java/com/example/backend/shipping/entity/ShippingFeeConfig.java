package com.example.backend.shipping.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "shipping_fee_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingFeeConfig {
    @Id
    private Long id; // 항상 1

    @Column(nullable = false)
    private Long amount;
}