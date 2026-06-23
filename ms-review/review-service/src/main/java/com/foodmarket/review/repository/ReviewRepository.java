package com.foodmarket.review.repository;
import com.foodmarket.review.model.*; import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; import org.springframework.data.repository.query.Param;
import java.util.List;
public interface ReviewRepository extends JpaRepository<Review,Long> {
    boolean existsByOrderIdAndCustomerIdAndTargetType(Long orderId, Long customerId, TargetType type);
    List<Review> findByTargetIdAndTargetType(Long targetId, TargetType type);
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetId=:id AND r.targetType=:type")
    Double findAverageRating(@Param("id") Long targetId, @Param("type") TargetType type);
}
