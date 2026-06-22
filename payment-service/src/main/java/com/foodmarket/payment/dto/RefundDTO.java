package com.foodmarket.payment.dto;
import jakarta.validation.constraints.*; import lombok.Data;
@Data public class RefundDTO { @NotBlank(message="Motivo del reembolso obligatorio") private String reason; }