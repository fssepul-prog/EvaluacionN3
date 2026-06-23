package com.foodmarket.auth.controller;

import com.foodmarket.auth.dto.*;
import com.foodmarket.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Registro e inicio de sesión de usuarios")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta con email, contraseña y rol (CUSTOMER, RESTAURANT_OWNER, ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterDTO dto) {
        log.info("Solicitud de registro: {}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @Operation(summary = "Iniciar sesión",
            description = "Autentica al usuario y retorna un token JWT para usar en peticiones protegidas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso, retorna token JWT"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        log.info("Solicitud de login: {}", dto.getEmail());
        return ResponseEntity.ok(authService.login(dto));
    }
}
