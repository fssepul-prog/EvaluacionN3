package com.foodmarket.user.service;

import com.foodmarket.user.dto.*;
import com.foodmarket.user.exception.*;
import com.foodmarket.user.model.*;
import com.foodmarket.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * CAPA DE SERVICIO: logica de negocio de perfiles y direcciones
 * Logs con @Slf4j para trazabilidad del sistema.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserProfileRepository profileRepo;
    private final AddressRepository addressRepo;

    public UserProfileDTO createProfile(UserProfileDTO dto) {
        UserProfile p = profileRepo.findByUserId(dto.getUserId())
                .map(existing -> {
                    existing.setFullName(dto.getFullName());
                    existing.setPhone(dto.getPhone());
                    existing.setEmail(dto.getEmail());
                    existing.setRole(dto.getRole());
                    return existing;
                })
                .orElse(UserProfile.builder()
                        .userId(dto.getUserId()).fullName(dto.getFullName())
                        .phone(dto.getPhone()).email(dto.getEmail()).role(dto.getRole())
                        .build());
        profileRepo.save(p);
        log.info("Perfil guardado para usuario {}", dto.getUserId());
        return dto;
    }

    public UserProfileDTO getProfile(Long userId) {
        UserProfile p = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado: " + userId));
        return UserProfileDTO.builder()
                .userId(p.getUserId()).fullName(p.getFullName())
                .phone(p.getPhone()).email(p.getEmail()).role(p.getRole())
                .build();
    }

    public Address addAddress(Long userId, AddressDTO dto) {
        Address a = Address.builder()
                .userId(userId).street(dto.getStreet())
                .city(dto.getCity()).zone(dto.getZone())
                .active(true).defaultAddr(dto.isDefaultAddr())
                .build();
        addressRepo.save(a);
        log.info("Direccion agregada para usuario {}: {}", userId, dto.getStreet());
        return a;
    }

    public List<Address> getAddresses(Long userId) {
        return addressRepo.findByUserIdAndActiveTrue(userId);
    }

    public void deleteAddress(Long addressId, Long userId) {
        Address a = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion no encontrada: " + addressId));
        if (!a.getUserId().equals(userId)) {
            throw new BusinessException("No autorizado para eliminar esta direccion");
        }
        a.setActive(false);
        addressRepo.save(a);
        log.info("Direccion {} desactivada para usuario {}", addressId, userId);
    }
}
