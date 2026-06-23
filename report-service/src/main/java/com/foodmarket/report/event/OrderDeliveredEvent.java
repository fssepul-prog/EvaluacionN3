package com.foodmarket.report.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveredEvent {
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private String status;
    private BigDecimal totalAmount;
    private String eventType;
}
