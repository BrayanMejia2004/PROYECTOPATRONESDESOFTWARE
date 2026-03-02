package com.gobierno.servicio_identidad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.gobierno.servicio_identidad.Infrastructure.Configuration.SeguridadConfig;

@SpringBootApplication
public class ServicioIdentidadApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioIdentidadApplication.class, args);

	}
}
