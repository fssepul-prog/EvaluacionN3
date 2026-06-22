package com.foodmarket.payment.dto;
import jakarta.validation.constraints.*; import lombok.*; import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentDTO {
    @NotNull(message="ID de orden obligatorio") private Long orderId;
    @NotNull(message="ID de cliente obligatorio") private Long customerId;
    @NotNull @DecimalMin(value="0.01",message="Monto debe ser mayor a 0") private BigDecimal amount;
    @NotNull @DecimalMin(value="0.00") private BigDecimal deliveryFee;
    @NotBlank(message="Metodo de pago obligatorio") private String method;
}