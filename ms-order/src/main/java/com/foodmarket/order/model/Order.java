package com.foodmarket.order.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
// Entidad con relaciones JPA y @PrePersist
@Entity @Table(name="orders") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="customer_id",nullable=false) private Long customerId;
    @Column(name="restaurant_id",nullable=false) private Long restaurantId;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private OrderStatus status;
    @Column(name="total_amount",precision=10,scale=2) private BigDecimal totalAmount;
    @Column(name="delivery_fee",precision=10,scale=2) private BigDecimal deliveryFee;
    @Column(name="delivery_address",nullable=false) private String deliveryAddress;
    @Column(name="delivery_zone") private String deliveryZone;
    @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<OrderItem> items = new ArrayList<>();
    @Column(name="created_at") private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @PrePersist public void prePersist() { this.createdAt=LocalDateTime.now(); this.status=OrderStatus.PENDING; }
    @PreUpdate public void preUpdate() { this.updatedAt=LocalDateTime.now(); }
}