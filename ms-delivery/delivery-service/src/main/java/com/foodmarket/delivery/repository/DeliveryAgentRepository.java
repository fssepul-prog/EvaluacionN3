package com.foodmarket.delivery.repository;
import com.foodmarket.delivery.model.DeliveryAgent; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent,Long> {
    Optional<DeliveryAgent> findFirstByZoneAndActiveTrue(String zone);
    List<DeliveryAgent> findAllByZoneAndActiveTrue(String zone);
}