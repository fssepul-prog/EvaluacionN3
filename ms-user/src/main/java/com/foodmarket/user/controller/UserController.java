package com.foodmarket.user.controller;

import com.foodmarket.user.dto.*;
import com.foodmarket.user.model.Address;
import com.foodmarket.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> createProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileDTO dto) {
        dto.setUserId(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createProfile(dto));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PostMapping("/{userId}/addresses")
    public ResponseEntity<Address> addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressDTO dto) {
        log.info("Agregando direccion para usuario {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addAddress(userId, dto));
    }

    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<Address>> getAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getAddresses(userId));
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        userService.deleteAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }
}
