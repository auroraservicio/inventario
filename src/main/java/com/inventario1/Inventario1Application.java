package com.inventario1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Inventario1Application {
    public static void main(String[] args) {
        SpringApplication.run(Inventario1Application.class, args);
        System.out.println("🚀 Sistema de Inventario 1 iniciado en http://localhost:8081");
    }
}
