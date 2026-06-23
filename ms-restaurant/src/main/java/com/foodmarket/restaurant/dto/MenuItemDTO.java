package com.foodmarket.restaurant.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MenuItemDTO {
    private Long id;
    @NotBlank(message="Nombre del plato obligatorio") private String name;
    @DecimalMin(value="0.01",message="Precio debe ser mayor a 0") @NotNull private BigDecimal price;
    @Min(value=0,message="Stock no puede ser negativo") private int stock;
    private boolean available;
    private String imageUrl;
    private String description;
}