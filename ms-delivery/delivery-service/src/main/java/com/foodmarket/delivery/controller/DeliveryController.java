package com.foodmarket.delivery.controller;

import com.foodmarket.delivery.dto.*;
import com.foodmarket.delivery.model.DeliveryStatus;
import com.foodmarket.delivery.service.DeliveryService;
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

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Entregas", description = "Gestión de repartidores y entregas de pedidos")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "Registrar nuevo repartidor")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Repartidor registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/agents")
    public ResponseEntity<DeliveryResponseDTO> registerAgent(@Valid @RequestBody AgentDTO dto) {
        log.info("Registrando repartidor: {}", dto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.registerAgent(dto));
    }

    @Operation(summary = "Asignar repartidor a un pedido",
            description = "Busca el primer repartidor disponible en la zona indicada")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Repartidor asignado exitosamente"),
        @ApiResponse(responseCode = "422", description = "Sin repartidores disponibles en la zona")
    })
    @PostMapping("/assign")
    public ResponseEntity<DeliveryResponseDTO> assign(@Valid @RequestBody AssignDeliveryDTO dto) {
        log.info("Asignando repartidor para pedido {} en zona {}", dto.getOrderId(), dto.getZone());
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.assign(dto));
    }

    @Operation(summary = "Actualizar estado de la entrega",
            description = "Estados válidos: ASSIGNED, IN_TRANSIT, DELIVERED, FAILED")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "404", description = "Entrega no encontrada")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<DeliveryResponseDTO> updateStatus(
            @Parameter(description = "ID de la entrega") @PathVariable Long id,
            @Parameter(description = "Nuevo estado") @RequestParam DeliveryStatus status) {
        log.info("Estado entrega {} -> {}", id, status);
        return ResponseEntity.ok(deliveryService.updateStatus(id, status));
    }

    @Operation(summary = "Obtener entrega por ID de pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entrega encontrada"),
        @ApiResponse(responseCode = "404", description = "Entrega no encontrada")
    })
    @GetMapping("/order/{id}")
    public ResponseEntity<DeliveryResponseDTO> getByOrder(
            @Parameter(description = "ID del pedido") @PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getByOrder(id));
    }
}
