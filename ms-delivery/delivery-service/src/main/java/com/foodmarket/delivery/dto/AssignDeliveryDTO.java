package com.foodmarket.delivery.dto;
import jakarta.validation.constraints.*; import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class AssignDeliveryDTO { @NotNull private Long orderId; @NotBlank private String zone; }