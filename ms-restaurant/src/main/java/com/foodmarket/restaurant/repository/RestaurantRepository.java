package com.foodmarket.restaurant.repository;
import com.foodmarket.restaurant.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
// Query Methods y @Query personalizados
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByZoneAndStatus(String zone, RestaurantStatus status);
    List<Restaurant> findByCategoryAndStatus(String category, RestaurantStatus status);
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%',:name,'%')) AND r.status = 'OPEN'")
    List<Restaurant> searchByName(@Param("name") String name);
}