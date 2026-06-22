package com.foodmarket.delivery.model;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="deliveries") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Delivery {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="order_id",nullable=false) private Long orderId;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="agent_id",nullable=false) private DeliveryAgent agent;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private DeliveryStatus status;
    @Column(name="assigned_at") private LocalDateTime assignedAt;
    @Column(name="delivered_at") private LocalDateTime deliveredAt;
    @PrePersist public void prePersist() { this.assignedAt=LocalDateTime.now(); }
}