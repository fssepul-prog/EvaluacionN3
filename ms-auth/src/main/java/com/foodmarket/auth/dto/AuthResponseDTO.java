package com.foodmarket.auth.dto;
import lombok.*;
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AuthResponseDTO {
    private String token;
    private String email;
    private String role;
    private String message;
}
