package com.example.backend.orderHistory.service;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.orderHistory.dto.MenuSalesByUserDto;
import com.example.backend.orderHistoryDetail.repository.OrderHistoryDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderReportService {

    private final OrderHistoryDetailRepository detailRepository;

    public Page<MenuSalesByUserDto> getMenuSalesByUser(
            Long menuId,
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        return detailRepository.findMenuSalesByUser(menuId, status, from, to, pageable);
    }
}