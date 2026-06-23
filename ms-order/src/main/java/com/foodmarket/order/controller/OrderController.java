package com.foodmarket.order.controller;

import com.foodmarket.order.dto.*;
import com.foodmarket.order.model.OrderStatus;
import com.foodmarket.order.service.OrderService;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pedidos", description = "Gestión de pedidos de comida")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Crear nuevo pedido",
            description = "Crea un pedido verificando restaurante abierto, zona y stock. Publica evento Kafka ORDER_CREATED.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "422", description = "Restaurante cerrado, fuera de zona o sin stock"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(
            @Valid @RequestBody CreateOrderDTO dto,
            @RequestHeader(value = "X-User-Email", defaultValue = "anonymous") String email,
            @RequestHeader(value = "X-User-Role", defaultValue = "CUSTOMER") String role) {
        log.info("Crear pedido por: {}", email);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(dto, 1L));
    }

    @Operation(summary = "Obtener pedido por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getById(
            @Parameter(description = "ID del pedido") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @Operation(summary = "Obtener pedidos de un cliente")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos del cliente ordenados por fecha")
    @GetMapping("/customer/{id}")
    public ResponseEntity<List<OrderResponseDTO>> getByCustomer(
            @Parameter(description = "ID del cliente") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getByCustomer(id));
    }

    @Operation(summary = "Actualizar estado del pedido",
            description = "Transiciones válidas: PENDING→CONFIRMED/CANCELLED, CONFIRMED→PREPARING/CANCELLED, PREPARING→READY, READY→IN_DELIVERY, IN_DELIVERY→DELIVERED")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "422", description = "Transición de estado inválida"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @Parameter(description = "ID del pedido") @PathVariable Long id,
            @Parameter(description = "Nuevo estado del pedido") @RequestParam OrderStatus newStatus,
            @RequestHeader(value = "X-User-Role", defaultValue = "ADMIN") String role) {
        log.info("Cambio estado pedido {} a {}", id, newStatus);
        return ResponseEntity.ok(orderService.updateStatus(id, newStatus, role));
    }
}
