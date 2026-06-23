package com.foodmarket.restaurant.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
@Entity @Table(name="menu_items") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MenuItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="restaurant_id",nullable=false) private Restaurant restaurant;
    @Column(nullable=false,length=100) private String name;
    @DecimalMin("0.01") @Column(precision=10,scale=2,nullable=false) private BigDecimal price;
    @Min(0) @Column(nullable=false) private int stock;
    @Column(nullable=false) private boolean available = true;
    @Column(name="image_url") private String imageUrl;
    @Column(length=500) private String description;
}