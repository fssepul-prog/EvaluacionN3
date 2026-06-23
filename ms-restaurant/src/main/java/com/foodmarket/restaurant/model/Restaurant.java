package com.foodmarket.restaurant.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.*;
@Entity @Table(name="restaurants") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Restaurant {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="owner_id",nullable=false) private Long ownerId;
    @Column(nullable=false,length=100) private String name;
    @Column(length=50) private String category;
    @Column(length=100) private String zone;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private RestaurantStatus status = RestaurantStatus.OPEN;
    @Column(name="open_time") private LocalTime openTime;
    @Column(name="close_time") private LocalTime closeTime;
    @OneToMany(mappedBy="restaurant", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<MenuItem> menuItems = new ArrayList<>();
}