package com.foodmarket.restaurant.repository;
import com.foodmarket.restaurant.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    // Query Method: solo items "DISPONIBLES"
    List<MenuItem> findByRestaurantIdAndAvailableTrue(Long restaurantId);
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :rid AND m.stock > 0")
    List<MenuItem> findAvailableByRestaurant(@Param("rid") Long restaurantId);
}