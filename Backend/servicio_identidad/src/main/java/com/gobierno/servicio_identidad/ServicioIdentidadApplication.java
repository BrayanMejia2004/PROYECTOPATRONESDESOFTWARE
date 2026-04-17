package com.gobierno.servicio_identidad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ServicioIdentidadApplication { // Clase principal del servicio de identidad

	public static void main(String[] args) { // Método main - punto de entrada de la aplicación
		SpringApplication.run(ServicioIdentidadApplication.class, args); // Inicia la aplicación Spring Boot
	}
}