package com.gobierno.servicio_identidad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ServicioIdentidadApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioIdentidadApplication.class, args);

	}
}
