package com.example.backend.shipping.repository;

import com.example.backend.shipping.entity.ShippingFeeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingFeeConfigRepository extends JpaRepository<ShippingFeeConfig, Long> {
}