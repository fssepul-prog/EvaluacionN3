package com.foodmarket.order.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateOrderDTO {
    @NotNull(message="ID de restaurante obligatorio") private Long restaurantId;
    @NotBlank(message="Direccion de entrega obligatoria") private String deliveryAddress;
    @NotBlank(message="Zona de entrega obligatoria") private String deliveryZone;
    @NotEmpty(message="El pedido debe tener al menos un item") private List<OrderItemDTO> items;
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderItemDTO {
        @NotNull private Long menuItemId;
        @Min(value=1,message="Cantidad minima es 1") private int quantity;
    }
}