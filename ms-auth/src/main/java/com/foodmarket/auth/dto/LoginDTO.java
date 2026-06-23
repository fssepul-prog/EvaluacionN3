package com.foodmarket.auth.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class LoginDTO {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email invalido")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    private String password;
}
