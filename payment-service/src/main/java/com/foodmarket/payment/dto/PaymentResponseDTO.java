package com.foodmarket.payment.dto;
import com.foodmarket.payment.model.PaymentStatus; import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentResponseDTO { private Long id,orderId,customerId; private BigDecimal amount,deliveryFee,totalAmount; private String method; private PaymentStatus status; private LocalDateTime createdAt; }