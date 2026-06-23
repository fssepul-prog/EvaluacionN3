package com.foodmarket.order.repository;
import com.foodmarket.order.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    @Query("SELECT o FROM Order o WHERE o.customerId=:cid AND o.status=:status")
    List<Order> findByCustomerAndStatus(@Param("cid") Long customerId, @Param("status") OrderStatus status);
}