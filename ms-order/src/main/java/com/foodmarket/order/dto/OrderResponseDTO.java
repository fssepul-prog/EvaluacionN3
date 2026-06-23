package com.foodmarket.order.dto;
import com.foodmarket.order.model.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponseDTO {
    private Long id; private Long customerId; private Long restaurantId;
    private OrderStatus status; private BigDecimal totalAmount; private BigDecimal deliveryFee;
    private String deliveryAddress; private String deliveryZone;
    private List<ItemDTO> items; private LocalDateTime createdAt;
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ItemDTO {
        private Long id; private Long menuItemId; private String itemName;
        private int quantity; private BigDecimal unitPrice;
    }
}