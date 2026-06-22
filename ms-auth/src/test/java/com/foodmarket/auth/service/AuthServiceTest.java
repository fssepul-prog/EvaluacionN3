package com.foodmarket.auth.service;

import com.foodmarket.auth.dto.AuthResponseDTO;
import com.foodmarket.auth.dto.LoginDTO;
import com.foodmarket.auth.dto.RegisterDTO;
import com.foodmarket.auth.exception.EmailAlreadyExistsException;
import com.foodmarket.auth.exception.InvalidCredentialsException;
import com.foodmarket.auth.model.Role;
import com.foodmarket.auth.model.User;
import com.foodmarket.auth.repository.UserRepository;
import com.foodmarket.auth.security.JwtUtil;
import com.foodmarket.auth.security.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Pruebas unitarias")
class AuthServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;
    private User existingUser;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@foodmarket.cl");
        registerDTO.setPassword("password123");
        registerDTO.setRole(Role.CUSTOMER);

        loginDTO = new LoginDTO();
        loginDTO.setEmail("test@foodmarket.cl");
        loginDTO.setPassword("password123");

        existingUser = User.builder()
                .email("test@foodmarket.cl")
                .password(PasswordUtil.encode("password123"))
                .role(Role.CUSTOMER)
                .build();
    }

    // ────────── register ──────────

    @Test
    @DisplayName("registro exitoso - email nuevo retorna AuthResponseDTO sin token")
    void register_conEmailNuevo_retornaAuthResponseDTO() {
        // Given
        when(userRepo.existsByEmail(registerDTO.getEmail())).thenReturn(false);
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        AuthResponseDTO result = authService.register(registerDTO);

        // Then
        assertNotNull(result);
        assertEquals("test@foodmarket.cl", result.getEmail());
        assertEquals("CUSTOMER", result.getRole());
        assertEquals("Registro exitoso", result.getMessage());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("registro falla - email ya registrado lanza EmailAlreadyExistsException")
    void register_conEmailExistente_lanzaEmailAlreadyExistsException() {
        // Given
        when(userRepo.existsByEmail(registerDTO.getEmail())).thenReturn(true);

        // When / Then
        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(registerDTO));
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("registro - la contraseña se almacena hasheada, nunca en texto plano")
    void register_laPasswordSeAlmacenaHasheada() {
        // Given
        when(userRepo.existsByEmail(any())).thenReturn(false);
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        authService.register(registerDTO);

        // Then
        verify(userRepo).save(argThat(user ->
                user.getPassword() != null &&
                !user.getPassword().equals("password123") &&
                PasswordUtil.matches("password123", user.getPassword())
        ));
    }

    // ────────── login ──────────

    @Test
    @DisplayName("login exitoso - credenciales correctas retorna token JWT")
    void login_conCredencialesCorrectas_retornaToken() {
        // Given
        when(userRepo.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token-mock");

        // When
        AuthResponseDTO result = authService.login(loginDTO);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token-mock", result.getToken());
        assertEquals("test@foodmarket.cl", result.getEmail());
        assertEquals("CUSTOMER", result.getRole());
        assertEquals("Login exitoso", result.getMessage());
    }

    @Test
    @DisplayName("login falla - email no existe lanza InvalidCredentialsException")
    void login_conEmailInexistente_lanzaInvalidCredentialsException() {
        // Given
        when(userRepo.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginDTO));
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("login falla - contraseña incorrecta lanza InvalidCredentialsException")
    void login_conPasswordIncorrecta_lanzaInvalidCredentialsException() {
        // Given
        loginDTO.setPassword("wrongpassword");
        when(userRepo.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(existingUser));

        // When / Then
        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginDTO));
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("login - se invoca generacion de token con email y rol correctos")
    void login_invocaGeneracionTokenConEmailYRol() {
        // Given
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("token");

        // When
        authService.login(loginDTO);

        // Then
        verify(jwtUtil).generateToken("test@foodmarket.cl", "CUSTOMER");
    }
}
