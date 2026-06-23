package com.foodmarket.report.kafka;

import com.foodmarket.report.event.OrderDeliveredEvent;
import com.foodmarket.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDeliveredConsumer {

    private final ReportService reportService;

    @KafkaListener(topics = "order-delivered", groupId = "report-service",
            containerFactory = "orderDeliveredListenerFactory")
    public void handleOrderDelivered(OrderDeliveredEvent event) {
        log.info("[REPORT-KAFKA] Pedido entregado recibido: orderId={}, restaurantId={}",
                event.getOrderId(), event.getRestaurantId());
        reportService.recordDeliveredOrder(
                event.getOrderId(),
                event.getRestaurantId(),
                event.getStatus(),
                event.getTotalAmount()
        );
    }
}
