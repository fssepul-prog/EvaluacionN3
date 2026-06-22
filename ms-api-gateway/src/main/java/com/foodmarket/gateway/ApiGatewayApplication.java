package com.foodmarket.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API GATEWAY
 * Verificar mediante GET http://localhost:8080/actuator/health  "status":"UP", toda la comunicacion pasa por este puerto.
 * (el puerto puede presentar cambios debido facilida de uso para los computadores de duoc)
 */
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
