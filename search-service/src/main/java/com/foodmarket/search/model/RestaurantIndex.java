package com.foodmarket.search.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="restaurant_index") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RestaurantIndex {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="restaurant_id",unique=true) private Long restaurantId;
    @Column(nullable=false,length=100) private String name;
    @Column(length=50) private String category;
    @Column(length=100) private String zone;
    @Column(nullable=false,length=20) private String status;
    @Column(name="avg_rating") private Double avgRating=0.0;
}