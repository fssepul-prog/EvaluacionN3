package com.foodmarket.report.controller;

import com.foodmarket.report.model.OrderSummary;
import com.foodmarket.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reportes", description = "Reportes de pedidos entregados (solo ADMIN)")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Obtener reporte de pedidos entregados por restaurante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte retornado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - se requiere rol ADMIN")
    })
    @GetMapping("/restaurant/{id}")
    public ResponseEntity<List<OrderSummary>> getByRestaurant(
            @Parameter(description = "ID del restaurante") @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", defaultValue = "CUSTOMER") String role) {
        if (!"ADMIN".equals(role)) {
            log.warn("[REPORT] Acceso denegado a reporte por restaurante. Rol: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reportService.getByRestaurant(id));
    }

    @Operation(summary = "Obtener reporte global de todos los pedidos entregados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte global retornado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - se requiere rol ADMIN")
    })
    @GetMapping("/all")
    public ResponseEntity<List<OrderSummary>> getAll(
            @RequestHeader(value = "X-User-Role", defaultValue = "CUSTOMER") String role) {
        if (!"ADMIN".equals(role)) {
            log.warn("[REPORT] Acceso denegado a reporte global. Rol: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reportService.getAll());
    }
}
