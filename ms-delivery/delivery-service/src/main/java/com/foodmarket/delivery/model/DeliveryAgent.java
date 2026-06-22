package com.foodmarket.delivery.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="delivery_agents") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryAgent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="user_id",nullable=false) private Long userId;
    @Column(nullable=false,length=100) private String zone;
    @Column(name="vehicle_type",length=50) private String vehicleType;
    @Column(name="is_active") private boolean active=true;
    @Column(length=100) private String name;
    @Column(length=150) private String email;
}