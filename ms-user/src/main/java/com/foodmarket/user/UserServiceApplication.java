package com.foodmarket.user;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
/** SERVICIO DE USUARIO - Perfiles y Direcciones | Puerto: 8082 | BD: db_user
 * 9082 puerto actual*/
@SpringBootApplication @EnableDiscoveryClient
public class UserServiceApplication { public static void main(String[] a) { SpringApplication.run(UserServiceApplication.class, a); } }
