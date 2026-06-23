package com.foodmarket.review.dto;
import com.foodmarket.review.model.TargetType; import jakarta.validation.constraints.*; import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewDTO { @NotNull private Long orderId,customerId,targetId; @NotNull private TargetType targetType; @Min(1) @Max(5) @NotNull private Integer rating; @Size(max=500) private String comment; }