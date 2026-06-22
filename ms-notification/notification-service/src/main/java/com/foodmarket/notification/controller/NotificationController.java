package com.foodmarket.notification.controller;

import com.foodmarket.notification.model.Notification;
import com.foodmarket.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notificaciones", description = "Gestión de notificaciones de usuarios")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Obtener todas las notificaciones de un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de notificaciones retornada exitosamente")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getByUser(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getByUser(userId));
    }

    @Operation(summary = "Obtener notificaciones no leídas de un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de notificaciones no leídas")
    })
    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnread(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnread(userId));
    }

    @Operation(summary = "Marcar una notificación como leída")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificación marcada como leída"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "ID de la notificación") @PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
