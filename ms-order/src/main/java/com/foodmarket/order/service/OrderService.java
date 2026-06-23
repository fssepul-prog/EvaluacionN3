package com.foodmarket.order.service;

import com.foodmarket.order.client.RestaurantFeignClient;
import com.foodmarket.order.dto.*;
import com.foodmarket.order.event.OrderEvent;
import com.foodmarket.order.exception.*;
import com.foodmarket.order.kafka.OrderEventProducer;
import com.foodmarket.order.model.*;
import com.foodmarket.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class OrderService {

    private final OrderRepository orderRepo;
    private final RestaurantFeignClient restaurantClient;
    private final OrderEventProducer eventProducer;

    public OrderResponseDTO createOrder(CreateOrderDTO dto, Long customerId) {
        RestaurantClientDTO restaurant = restaurantClient.getRestaurant(dto.getRestaurantId());

        if ("CLOSED".equals(restaurant.getStatus()))
            throw new BusinessException("El restaurante esta cerrado y no acepta pedidos");

        if (!restaurant.getZone().equals(dto.getDeliveryZone()))
            throw new BusinessException("Direccion fuera de la zona del restaurante. Zona: " + restaurant.getZone());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderDTO.OrderItemDTO itemDto : dto.getItems()) {
            MenuItemClientDTO menu = restaurantClient.getMenuItem(dto.getRestaurantId(), itemDto.getMenuItemId());
            if (!menu.isAvailable() || menu.getStock() == 0)
                throw new BusinessException("Sin stock: " + menu.getName());
            OrderItem oi = OrderItem.builder()
                    .menuItemId(menu.getId()).itemName(menu.getName())
                    .quantity(itemDto.getQuantity()).unitPrice(menu.getPrice()).build();
            items.add(oi);
            total = total.add(menu.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        BigDecimal fee = new BigDecimal("1990");
        Order order = Order.builder()
                .customerId(customerId).restaurantId(dto.getRestaurantId())
                .deliveryAddress(dto.getDeliveryAddress()).deliveryZone(dto.getDeliveryZone())
                .totalAmount(total.add(fee)).deliveryFee(fee).build();
        items.forEach(i -> i.setOrder(order));
        order.setItems(items);
        Order saved = orderRepo.save(order);
        log.info("Pedido {} creado para cliente {}", saved.getId(), customerId);

        eventProducer.publishOrderCreated(OrderEvent.builder()
                .orderId(saved.getId()).customerId(saved.getCustomerId())
                .restaurantId(saved.getRestaurantId()).status(saved.getStatus().name())
                .totalAmount(saved.getTotalAmount()).build());

        return toDTO(saved);
    }

    public OrderResponseDTO getById(Long id) {
        return toDTO(orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id)));
    }

    public List<OrderResponseDTO> getByCustomer(Long customerId) {
        return orderRepo.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public OrderResponseDTO updateStatus(Long orderId, OrderStatus newStatus, String role) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        if (!isValidTransition(order.getStatus(), newStatus))
            throw new BusinessException("Transicion invalida: " + order.getStatus() + " -> " + newStatus);
        if (newStatus == OrderStatus.CANCELLED) {
            boolean canCancel = "ADMIN".equals(role) ||
                    order.getStatus() == OrderStatus.PENDING ||
                    order.getStatus() == OrderStatus.CONFIRMED;
            if (!canCancel)
                throw new BusinessException("Solo ADMIN puede cancelar en este estado");
        }
        order.setStatus(newStatus);
        orderRepo.save(order);
        log.info("Pedido {} -> estado {}", orderId, newStatus);

        OrderEvent event = OrderEvent.builder()
                .orderId(order.getId()).customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId()).status(newStatus.name())
                .totalAmount(order.getTotalAmount()).build();

        if (newStatus == OrderStatus.DELIVERED) {
            eventProducer.publishOrderDelivered(event);
        } else {
            eventProducer.publishStatusChanged(event);
        }

        return toDTO(order);
    }

    private boolean isValidTransition(OrderStatus c, OrderStatus n) {
        return switch (c) {
            case PENDING    -> n == OrderStatus.CONFIRMED  || n == OrderStatus.CANCELLED;
            case CONFIRMED  -> n == OrderStatus.PREPARING  || n == OrderStatus.CANCELLED;
            case PREPARING  -> n == OrderStatus.READY;
            case READY      -> n == OrderStatus.IN_DELIVERY;
            case IN_DELIVERY-> n == OrderStatus.DELIVERED;
            default         -> false;
        };
    }

    private OrderResponseDTO toDTO(Order o) {
        List<OrderResponseDTO.ItemDTO> items = o.getItems().stream().map(i ->
                OrderResponseDTO.ItemDTO.builder()
                        .id(i.getId()).menuItemId(i.getMenuItemId())
                        .itemName(i.getItemName()).quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice()).build()
        ).collect(Collectors.toList());
        return OrderResponseDTO.builder()
                .id(o.getId()).customerId(o.getCustomerId()).restaurantId(o.getRestaurantId())
                .status(o.getStatus()).totalAmount(o.getTotalAmount()).deliveryFee(o.getDeliveryFee())
                .deliveryAddress(o.getDeliveryAddress()).deliveryZone(o.getDeliveryZone())
                .items(items).createdAt(o.getCreatedAt()).build();
    }
}
