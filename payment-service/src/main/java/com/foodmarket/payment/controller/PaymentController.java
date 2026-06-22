package com.foodmarket.payment.controller;
import com.foodmarket.payment.dto.*; import com.foodmarket.payment.service.PaymentService;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/payments") @RequiredArgsConstructor @Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping public ResponseEntity<PaymentResponseDTO> process(@Valid @RequestBody PaymentDTO dto) { log.info("Procesando pago para orden {}", dto.getOrderId()); return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(dto)); }
    @GetMapping("/order/{id}") public ResponseEntity<PaymentResponseDTO> getByOrder(@PathVariable Long id) { return ResponseEntity.ok(paymentService.getByOrder(id)); }
    @GetMapping("/customer/{id}") public ResponseEntity<List<PaymentResponseDTO>> getByCustomer(@PathVariable Long id) { return ResponseEntity.ok(paymentService.getByCustomer(id)); }
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponseDTO> refund(@PathVariable Long id, @Valid @RequestBody RefundDTO dto, @RequestHeader(value="X-User-Role", defaultValue="ADMIN") String role) {
        if (!"ADMIN".equals(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        log.info("Reembolso solicitado por ADMIN para pago {}", id);
        return ResponseEntity.ok(paymentService.refund(id, dto));
    }
}