package com.foodmarket.delivery.dto;
import com.foodmarket.delivery.model.DeliveryStatus; import lombok.*; import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryResponseDTO { private Long id,orderId,agentId; private String agentName,agentZone; private DeliveryStatus status; private LocalDateTime assignedAt,deliveredAt; }