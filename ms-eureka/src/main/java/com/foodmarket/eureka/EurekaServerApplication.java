package com.foodmarket.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Todos los microservicios se registran aqui al iniciar.
 * Verificar en: http://localhost:8761      / 9761 (ACTUAL)
 * * Permite que los servicios se encuentren entre si
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
