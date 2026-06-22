package com.foodmarket.user.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AddressDTO {
    @NotBlank(message="Calle obligatoria") private String street;
    @NotBlank(message="Ciudad obligatoria") private String city;
    @NotBlank(message="Zona obligatoria") private String zone;
    private boolean defaultAddr;
}
