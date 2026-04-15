package com.gobierno.servicio_autorizacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ServicioAutorizacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioAutorizacionApplication.class, args);
	}

}
