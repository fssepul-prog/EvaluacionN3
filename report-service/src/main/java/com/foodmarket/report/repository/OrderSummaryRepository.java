package com.foodmarket.report.repository;
import com.foodmarket.report.model.OrderSummary; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface OrderSummaryRepository extends JpaRepository<OrderSummary,Long> {
    List<OrderSummary> findByRestaurantIdOrderByOccurredAtDesc(Long restaurantId);
}