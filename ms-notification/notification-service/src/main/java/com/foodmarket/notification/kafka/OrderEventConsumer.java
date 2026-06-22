package com.foodmarket.notification.kafka;

import com.foodmarket.notification.event.OrderEvent;
import com.foodmarket.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order-events", groupId = "notification-service",
            containerFactory = "orderEventListenerFactory")
    public void handleOrderEvent(OrderEvent event) {
        log.info("[NOTIFICATION-KAFKA] Evento recibido: {} para pedido {}", event.getEventType(), event.getOrderId());
        String message = buildMessage(event);
        notificationService.create(event.getCustomerId(), event.getEventType(), message);
    }

    @KafkaListener(topics = "order-delivered", groupId = "notification-service-delivered",
            containerFactory = "orderEventListenerFactory")
    public void handleOrderDelivered(OrderEvent event) {
        log.info("[NOTIFICATION-KAFKA] Pedido {} entregado, notificando cliente {}", event.getOrderId(), event.getCustomerId());
        String message = "Tu pedido #" + event.getOrderId() + " ha sido entregado exitosamente. Total: $" + event.getTotalAmount();
        notificationService.create(event.getCustomerId(), "ORDER_DELIVERED", message);
    }

    private String buildMessage(OrderEvent event) {
        return switch (event.getEventType()) {
            case "ORDER_CREATED"  -> "Tu pedido #" + event.getOrderId() + " ha sido creado. Total: $" + event.getTotalAmount();
            case "ORDER_STATUS_UPDATED" -> buildStatusMessage(event);
            default -> "Actualizacion de pedido #" + event.getOrderId() + ": " + event.getStatus();
        };
    }

    private String buildStatusMessage(OrderEvent event) {
        return switch (event.getStatus()) {
            case "CONFIRMED"   -> "Tu pedido #" + event.getOrderId() + " ha sido confirmado por el restaurante.";
            case "PREPARING"   -> "Tu pedido #" + event.getOrderId() + " esta siendo preparado.";
            case "READY"       -> "Tu pedido #" + event.getOrderId() + " esta listo para ser despachado.";
            case "IN_DELIVERY" -> "Tu pedido #" + event.getOrderId() + " va en camino.";
            case "CANCELLED"   -> "Tu pedido #" + event.getOrderId() + " ha sido cancelado.";
            default -> "Tu pedido #" + event.getOrderId() + " cambio a estado: " + event.getStatus();
        };
    }
}
