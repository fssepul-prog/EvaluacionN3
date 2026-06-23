package com.foodmarket.order.kafka;

import com.foodmarket.order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private static final String TOPIC_ORDER_EVENTS = "order-events";
    private static final String TOPIC_ORDER_DELIVERED = "order-delivered";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void publishOrderCreated(OrderEvent event) {
        event.setEventType("ORDER_CREATED");
        kafkaTemplate.send(TOPIC_ORDER_EVENTS, String.valueOf(event.getOrderId()), event);
        log.info("[ORDER-KAFKA] Evento ORDER_CREATED publicado para pedido {}", event.getOrderId());
    }

    public void publishStatusChanged(OrderEvent event) {
        event.setEventType("ORDER_STATUS_UPDATED");
        kafkaTemplate.send(TOPIC_ORDER_EVENTS, String.valueOf(event.getOrderId()), event);
        log.info("[ORDER-KAFKA] Evento ORDER_STATUS_UPDATED publicado para pedido {} -> {}", event.getOrderId(), event.getStatus());
    }

    public void publishOrderDelivered(OrderEvent event) {
        event.setEventType("ORDER_DELIVERED");
        kafkaTemplate.send(TOPIC_ORDER_DELIVERED, String.valueOf(event.getOrderId()), event);
        log.info("[ORDER-KAFKA] Evento ORDER_DELIVERED publicado para pedido {}", event.getOrderId());
    }
}
