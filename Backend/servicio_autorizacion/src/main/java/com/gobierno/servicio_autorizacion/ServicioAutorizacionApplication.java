package com.gobierno.servicio_autorizacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ServicioAutorizacionApplication { // Clase principal del servicio de autorización

	public static void main(String[] args) { // Método main - punto de entrada de la aplicación
		SpringApplication.run(ServicioAutorizacionApplication.class, args); // Inicia la aplicación Spring Boot
	}

}