package com.foodmarket.auth.dto;
import com.foodmarket.auth.model.Role;
import jakarta.validation.constraints.*;
import lombok.Data;
/**
 * Bean Validation: @Valid en el controller activa estas
 * anotaciones. Si falla, GlobalExceptionHandler retorna 400 con mapa
 * de errores por campo.
 *
 * Esto se encarga de filtrar y detectar datos necesarios para cada campo, y advertir cuando el sistema
 * recibe un dato erroneo, conforme a las reglas de negocio.
 */
@Data
public class RegisterDTO {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email invalido")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 8, message = "Minimo 8 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Role role;
}
