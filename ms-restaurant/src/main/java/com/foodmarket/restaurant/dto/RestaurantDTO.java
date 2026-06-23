package com.foodmarket.restaurant.dto;
import com.foodmarket.restaurant.model.RestaurantStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RestaurantDTO {
    private Long id;
    @NotBlank(message="Nombre obligatorio") private String name;
    @NotBlank(message="Categoria obligatoria") private String category;
    @NotBlank(message="Zona obligatoria") private String zone;
    private RestaurantStatus status;
    @NotNull(message="Horario apertura obligatorio") private LocalTime openTime;
    @NotNull(message="Horario cierre obligatorio") private LocalTime closeTime;
}