package com.foodmarket.payment.model;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="payments") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="order_id",nullable=false) private Long orderId;
    @Column(name="customer_id",nullable=false) private Long customerId;
    @Column(precision=10,scale=2,nullable=false) private BigDecimal amount;
    @Column(name="delivery_fee",precision=10,scale=2) private BigDecimal deliveryFee;
    @Column(name="total_amount",precision=10,scale=2) private BigDecimal totalAmount;
    @Column(length=50) private String method;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private PaymentStatus status;
    @Column(name="refund_reason") private String refundReason;
    @Column(name="created_at") private LocalDateTime createdAt;
    @PrePersist public void prePersist() { this.createdAt=LocalDateTime.now(); }
}