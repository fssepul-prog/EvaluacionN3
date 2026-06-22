package com.foodmarket.user.model;
import jakarta.persistence.*; import lombok.*;
/** Entidad JPA: direcciones de entrega del usuario */
@Entity @Table(name="addresses") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Address {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="user_id", nullable=false) private Long userId;
    @Column(nullable=false, length=200) private String street;
    @Column(nullable=false, length=100) private String city;
    @Column(nullable=false, length=100) private String zone;
    @Column(name="is_active") private boolean active = true;
    @Column(name="is_default") private boolean defaultAddr = false;
}
