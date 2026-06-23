package com.foodmarket.restaurant.controller;

import com.foodmarket.restaurant.dto.*;
import com.foodmarket.restaurant.model.RestaurantStatus;
import com.foodmarket.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Restaurantes", description = "Gestión de restaurantes y menús")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(summary = "Crear restaurante", description = "Solo RESTAURANT_OWNER o ADMIN pueden crear restaurantes")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Restaurante creado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para crear restaurante")
    })
    @PostMapping
    public ResponseEntity<RestaurantDTO> create(
            @Valid @RequestBody RestaurantDTO dto,
            @RequestHeader(value = "X-User-Email", defaultValue = "anonymous") String email,
            @RequestHeader(value = "X-User-Role", defaultValue = "RESTAURANT_OWNER") String role) {
        if (!role.equals("RESTAURANT_OWNER") && !role.equals("ADMIN"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        log.info("Creando restaurante por: {}", email);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.create(dto, 1L));
    }

    @Operation(summary = "Obtener restaurante por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
        @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getById(
            @Parameter(description = "ID del restaurante") @PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getById(id));
    }

    @Operation(summary = "Buscar restaurantes por zona", description = "Retorna solo restaurantes con estado OPEN")
    @ApiResponse(responseCode = "200", description = "Lista de restaurantes abiertos en la zona")
    @GetMapping("/zone/{zone}")
    public ResponseEntity<List<RestaurantDTO>> getByZone(
            @Parameter(description = "Zona de búsqueda") @PathVariable String zone) {
        return ResponseEntity.ok(restaurantService.getByZone(zone));
    }

    @Operation(summary = "Agregar ítem al menú del restaurante")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ítem agregado al menú"),
        @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @PostMapping("/{id}/menu")
    public ResponseEntity<MenuItemDTO> addMenuItem(
            @Parameter(description = "ID del restaurante") @PathVariable Long id,
            @Valid @RequestBody MenuItemDTO dto) {
        log.info("Agregando item al menu del restaurante {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.addMenuItem(id, dto));
    }

    @Operation(summary = "Obtener menú disponible del restaurante")
    @ApiResponse(responseCode = "200", description = "Lista de ítems disponibles")
    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItemDTO>> getMenu(
            @Parameter(description = "ID del restaurante") @PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getAvailableMenu(id));
    }

    @Operation(summary = "Obtener ítem específico del menú")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ítem encontrado"),
        @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @GetMapping("/{id}/menu/{itemId}")
    public ResponseEntity<MenuItemDTO> getMenuItem(
            @PathVariable Long id,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(restaurantService.getMenuItemById(id, itemId));
    }

    @Operation(summary = "Actualizar stock de un ítem del menú")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock actualizado"),
        @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @PatchMapping("/{id}/menu/{itemId}/stock")
    public ResponseEntity<MenuItemDTO> updateStock(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Parameter(description = "Nueva cantidad en stock") @RequestParam int quantity) {
        return ResponseEntity.ok(restaurantService.updateStock(itemId, quantity));
    }

    @Operation(summary = "Actualizar estado del restaurante (OPEN/CLOSED)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado: OPEN o CLOSED") @RequestParam RestaurantStatus status) {
        restaurantService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
