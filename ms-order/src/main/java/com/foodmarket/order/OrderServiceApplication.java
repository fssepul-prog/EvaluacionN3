package com.foodmarket.order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
// @EnableFeignClients OBLIGATORIO para RestaurantFeignClient
@SpringBootApplication @EnableDiscoveryClient @EnableFeignClients
public class OrderServiceApplication {
    public static void main(String[] args) { SpringApplication.run(OrderServiceApplication.class, args); }
}