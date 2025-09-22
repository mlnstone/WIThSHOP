package com.example.backend.orderHistory.service;

import com.example.backend.common.enums.OrderStatus;
import com.example.backend.orderHistory.dto.MenuBestItemView;
import com.example.backend.orderHistory.dto.MenuSalesByUserDto;
import com.example.backend.orderHistory.dto.MenuSalesSummaryView;
import com.example.backend.orderHistoryDetail.repository.OrderHistoryDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public Page<MenuSalesSummaryView> getMenuSalesSummary(
            OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable
    ) {
        return detailRepository.findMenuSalesSummary(status, from, to, pageable);
    }

    public Page<MenuSalesSummaryView> getBestMenusForUsers(
            OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable
    ) {
        return detailRepository.findBestMenusForUsers(status, from, to, pageable);
    }

    public List<MenuBestItemView> getBestByCategory(
            Long categoryId,
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to,
            int limit
    ) {
        var page = detailRepository.findBestByCategory(
                categoryId, status, from, to,
                org.springframework.data.domain.PageRequest.of(0, Math.max(1, limit))
        );
        return page.getContent();
    }
}