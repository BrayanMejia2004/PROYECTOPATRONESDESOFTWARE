package com.gobierno.servicio_auditoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // Anotación que indica que esta es la clase principal de la aplicación Spring Boot
public class ServicioAuditoriaApplication {  // Clase principal del servicio de auditoría

	public static void main(String[] args) {  // Método main - punto de entrada de la aplicación
		SpringApplication.run(ServicioAuditoriaApplication.class, args);  // Inicia la aplicación Spring Boot
	}
}