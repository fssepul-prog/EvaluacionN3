package com.foodmarket.user.repository;
import com.foodmarket.user.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findByUserIdAndActiveTrue(Long userId);
}
