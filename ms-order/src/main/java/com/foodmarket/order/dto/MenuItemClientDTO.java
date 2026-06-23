package com.foodmarket.order.dto;
import lombok.*;
import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor
public class MenuItemClientDTO { private Long id; private String name; private BigDecimal price; private int stock; private boolean available; }