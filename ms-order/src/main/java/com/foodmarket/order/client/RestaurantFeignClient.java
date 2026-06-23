package com.foodmarket.order.client;
import com.foodmarket.order.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
// FeignClient: llama a restaurant-service via Eureka (NO mediante el localhost)
@FeignClient(name="ms-restaurant")
public interface RestaurantFeignClient {
    @GetMapping("/restaurants/{id}") RestaurantClientDTO getRestaurant(@PathVariable("id") Long id);
    @GetMapping("/restaurants/{restaurantId}/menu/{itemId}") MenuItemClientDTO getMenuItem(@PathVariable("restaurantId") Long restaurantId, @PathVariable("itemId") Long itemId);
}