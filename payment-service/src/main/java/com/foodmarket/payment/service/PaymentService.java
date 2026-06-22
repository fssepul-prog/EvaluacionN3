package com.foodmarket.payment.service;

import com.foodmarket.payment.dto.*;
import com.foodmarket.payment.exception.*;
import com.foodmarket.payment.model.*;
import com.foodmarket.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepo;

    public PaymentResponseDTO processPayment(PaymentDTO dto) {
        paymentRepo.findByOrderId(dto.getOrderId()).ifPresent(p -> {
            if (p.getStatus() == PaymentStatus.COMPLETED)
                throw new BusinessException("Ya existe pago completado para esta orden");
        });
        Payment p = Payment.builder()
                .orderId(dto.getOrderId()).customerId(dto.getCustomerId())
                .amount(dto.getAmount()).deliveryFee(dto.getDeliveryFee())
                .totalAmount(dto.getAmount().add(dto.getDeliveryFee()))
                .method(dto.getMethod()).status(PaymentStatus.PENDING).build();
        p = paymentRepo.save(p);
        if (Math.random() > 0.1) {
            p.setStatus(PaymentStatus.COMPLETED);
            log.info("Pago completado para orden {}", dto.getOrderId());
        } else {
            p.setStatus(PaymentStatus.FAILED);
            log.warn("Pago fallido para orden {}", dto.getOrderId());
        }
        return toDTO(paymentRepo.save(p));
    }

    public PaymentResponseDTO refund(Long paymentId, RefundDTO dto) {
        Payment p = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + paymentId));
        if (p.getStatus() != PaymentStatus.COMPLETED)
            throw new BusinessException("Solo se pueden reembolsar pagos completados");
        p.setStatus(PaymentStatus.REFUNDED);
        p.setRefundReason(dto.getReason());
        paymentRepo.save(p);
        log.info("Reembolso emitido para pago {}: {}", paymentId, dto.getReason());
        return toDTO(p);
    }

    public PaymentResponseDTO getByOrder(Long orderId) {
        return toDTO(paymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado para orden: " + orderId)));
    }

    public List<PaymentResponseDTO> getByCustomer(Long customerId) {
        return paymentRepo.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private PaymentResponseDTO toDTO(Payment p) {
        return PaymentResponseDTO.builder()
                .id(p.getId()).orderId(p.getOrderId()).customerId(p.getCustomerId())
                .amount(p.getAmount()).deliveryFee(p.getDeliveryFee())
                .totalAmount(p.getTotalAmount()).method(p.getMethod())
                .status(p.getStatus()).createdAt(p.getCreatedAt()).build();
    }
}
