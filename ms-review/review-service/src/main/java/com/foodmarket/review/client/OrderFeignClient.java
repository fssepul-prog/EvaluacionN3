package com.foodmarket.review.client;
import lombok.Data; import org.springframework.cloud.openfeign.FeignClient; import org.springframework.web.bind.annotation.*;
@FeignClient(name = "ms-order")
public interface OrderFeignClient {
    @GetMapping("/orders/{id}") OrderStatusDTO getOrder(@PathVariable("id") Long id);
    @Data class OrderStatusDTO { private Long id; private String status; }
}
