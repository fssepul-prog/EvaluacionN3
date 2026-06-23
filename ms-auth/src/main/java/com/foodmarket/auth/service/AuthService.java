package com.foodmarket.auth.service;

import com.foodmarket.auth.dto.*;
import com.foodmarket.auth.exception.*;
import com.foodmarket.auth.model.User;
import com.foodmarket.auth.repository.UserRepository;
import com.foodmarket.auth.security.JwtUtil;
import com.foodmarket.auth.security.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO register(RegisterDTO dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "El email ya esta registrado: " + dto.getEmail());
        }
        User user = User.builder()
                .email(dto.getEmail())
                .password(PasswordUtil.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();
        userRepo.save(user);
        log.info("Usuario registrado: {} con rol {}", dto.getEmail(), dto.getRole());
        return AuthResponseDTO.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Registro exitoso")
                .build();
    }

    public AuthResponseDTO login(LoginDTO dto) {
        User user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales invalidas"));
        if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales invalidas");
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        log.info("Login exitoso: {}", dto.getEmail());
        return AuthResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Login exitoso")
                .build();
    }
}
