package com.foodmarket.report.model;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="order_summaries") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderSummary {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="order_id") private Long orderId;
    @Column(name="restaurant_id") private Long restaurantId;
    @Column(length=30) private String status;
    @Column(name="occurred_at") private LocalDateTime occurredAt;
}