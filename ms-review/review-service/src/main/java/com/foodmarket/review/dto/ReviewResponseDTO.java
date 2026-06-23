package com.foodmarket.review.dto;
import com.foodmarket.review.model.TargetType; import lombok.*; import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewResponseDTO { private Long id,orderId,customerId,targetId; private TargetType targetType; private int rating; private String comment; private LocalDateTime createdAt; }