package com.foodmarket.auth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
/**
 * AUTH SERVICE - Autenticacion y Autorizacion
 * Maneja: registro, login, BCrypt, JWT
 * Puerto: 8081 | BD: db_auth
 *         9081 PUERTO ACTUAL DEBIDO A FUNCIONAMIENTO DE COMPUTADORES DUOC
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
