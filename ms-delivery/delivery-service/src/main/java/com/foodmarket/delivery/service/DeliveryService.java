package com.foodmarket.delivery.service;

import com.foodmarket.delivery.dto.*;
import com.foodmarket.delivery.exception.*;
import com.foodmarket.delivery.model.*;
import com.foodmarket.delivery.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service @RequiredArgsConstructor @Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepo;
    private final DeliveryAgentRepository agentRepo;

    public DeliveryResponseDTO registerAgent(AgentDTO dto) {
        DeliveryAgent a = DeliveryAgent.builder()
                .userId(dto.getUserId()).zone(dto.getZone())
                .vehicleType(dto.getVehicleType()).name(dto.getName())
                .email(dto.getEmail()).active(true).build();
        agentRepo.save(a);
        log.info("Repartidor registrado: {} en zona {}", a.getName(), a.getZone());
        return DeliveryResponseDTO.builder()
                .agentId(a.getId()).agentName(a.getName()).agentZone(a.getZone()).build();
    }

    public DeliveryResponseDTO assign(AssignDeliveryDTO dto) {
        DeliveryAgent agent = agentRepo.findFirstByZoneAndActiveTrue(dto.getZone())
                .orElseThrow(() -> new BusinessException("No hay repartidores en zona: " + dto.getZone()));
        Delivery d = Delivery.builder()
                .orderId(dto.getOrderId()).agent(agent).status(DeliveryStatus.ASSIGNED).build();
        deliveryRepo.save(d);
        log.info("Pedido {} asignado al repartidor {} en zona {}", dto.getOrderId(), agent.getId(), dto.getZone());
        return toDTO(d);
    }

    public DeliveryResponseDTO updateStatus(Long deliveryId, DeliveryStatus newStatus) {
        Delivery d = deliveryRepo.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada: " + deliveryId));
        d.setStatus(newStatus);
        if (newStatus == DeliveryStatus.DELIVERED) {
            d.setDeliveredAt(LocalDateTime.now());
            log.info("Entrega {} completada para orden {}", deliveryId, d.getOrderId());
        }
        deliveryRepo.save(d);
        return toDTO(d);
    }

    public DeliveryResponseDTO getByOrder(Long orderId) {
        return toDTO(deliveryRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada para orden: " + orderId)));
    }

    private DeliveryResponseDTO toDTO(Delivery d) {
        return DeliveryResponseDTO.builder()
                .id(d.getId()).orderId(d.getOrderId())
                .agentId(d.getAgent().getId()).agentName(d.getAgent().getName())
                .agentZone(d.getAgent().getZone()).status(d.getStatus())
                .assignedAt(d.getAssignedAt()).deliveredAt(d.getDeliveredAt()).build();
    }
}
