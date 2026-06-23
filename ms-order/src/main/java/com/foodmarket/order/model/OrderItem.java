package com.foodmarket.order.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Entity @Table(name="order_items") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="order_id",nullable=false) private Order order;
    @Column(name="menu_item_id",nullable=false) private Long menuItemId;
    @Column(name="item_name",nullable=false) private String itemName;
    @Column(nullable=false) private int quantity;
    @Column(name="unit_price",precision=10,scale=2,nullable=false) private BigDecimal unitPrice;
}