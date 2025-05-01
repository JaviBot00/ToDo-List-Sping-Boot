package com.hotguy.tareas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Marca esta clase como el punto de entrada del proyecto
public class TareasApplication {

    public static void main(String[] args) {
        SpringApplication.run(TareasApplication.class, args); // Arranca Spring Boot
    }
}
