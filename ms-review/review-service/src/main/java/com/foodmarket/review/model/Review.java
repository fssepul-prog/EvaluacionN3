package com.foodmarket.review.model;
import jakarta.persistence.*; import jakarta.validation.constraints.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="reviews") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="order_id",nullable=false) private Long orderId;
    @Column(name="customer_id",nullable=false) private Long customerId;
    @Column(name="target_id",nullable=false) private Long targetId;
    @Enumerated(EnumType.STRING) @Column(name="target_type",nullable=false) private TargetType targetType;
    @Min(1) @Max(5) @Column(nullable=false) private int rating;
    @Size(max=500) private String comment;
    @Column(name="created_at") private LocalDateTime createdAt;
    @PrePersist public void prePersist() { this.createdAt=LocalDateTime.now(); }
}