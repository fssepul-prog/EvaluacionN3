package com.foodmarket.report.service;

import com.foodmarket.report.model.OrderSummary;
import com.foodmarket.report.repository.OrderSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final OrderSummaryRepository summaryRepo;

    public OrderSummary recordDeliveredOrder(Long orderId, Long restaurantId, String status, BigDecimal totalAmount) {
        OrderSummary summary = OrderSummary.builder()
                .orderId(orderId)
                .restaurantId(restaurantId)
                .status(status)
                .occurredAt(LocalDateTime.now())
                .build();
        OrderSummary saved = summaryRepo.save(summary);
        log.info("[REPORT] Resumen registrado para pedido {} del restaurante {}", orderId, restaurantId);
        return saved;
    }

    public List<OrderSummary> getByRestaurant(Long restaurantId) {
        log.info("[REPORT] Consultando reportes para restaurante {}", restaurantId);
        return summaryRepo.findByRestaurantIdOrderByOccurredAtDesc(restaurantId);
    }

    public List<OrderSummary> getAll() {
        log.info("[REPORT] Consultando reporte global");
        return summaryRepo.findAll();
    }
}
