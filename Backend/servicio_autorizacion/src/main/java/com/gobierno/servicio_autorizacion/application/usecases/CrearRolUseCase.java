package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.factory.AbstractRolCreator;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolRepositoryPort;

@Service
public class CrearRolUseCase { // Caso de uso para crear un nuevo rol

    private final RolRepositoryPort rolRepositoryPort; // Puerto de repositorio de roles
    private final Map<String, AbstractRolCreator> creators; // Mapa de creators (USER, ADMIN, AUDITOR)

    public CrearRolUseCase(RolRepositoryPort rolRepositoryPort, // Constructor con inyección
            Map<String, AbstractRolCreator> creators) { // Spring inyecta los creators como mapa
        this.rolRepositoryPort = rolRepositoryPort; // Asigna el repositorio
        this.creators = creators; // Asigna los creators
    }

    public Rol ejecutar(String nombreRol, String descripcion) { // Método principal para crear un rol
        AbstractRolCreator creator = creators.get(nombreRol.toUpperCase()); // Busca el creator por el nombre del rol

        Rol rol; // Variable para almacenar el rol a crear
        if (creator != null) { // Si existe un creator para este tipo de rol
            rol = creator.crearRol(); // Usa el Factory Method para crear el rol
        } else { // Si no existe un creator
            rol = new Rol(nombreRol.toUpperCase(), descripcion); // Crea un rol personalizado
        }

        return rolRepositoryPort.guardar(rol); // Persiste el rol y lo retorna
    }

}