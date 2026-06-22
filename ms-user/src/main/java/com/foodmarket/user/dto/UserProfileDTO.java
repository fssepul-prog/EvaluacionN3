package com.foodmarket.user.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfileDTO {
    private Long userId;
    @NotBlank(message="Nombre obligatorio") private String fullName;
    private String phone;
    @Email private String email;
    private String role;
}
