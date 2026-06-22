package com.foodmarket.payment.repository;
import com.foodmarket.payment.model.Payment; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}