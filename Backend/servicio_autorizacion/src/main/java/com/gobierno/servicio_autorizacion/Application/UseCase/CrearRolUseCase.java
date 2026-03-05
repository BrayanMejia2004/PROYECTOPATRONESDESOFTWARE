package com.gobierno.servicio_autorizacion.Application.UseCase;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.Domain.Factory.RolFactory;
import com.gobierno.servicio_autorizacion.Domain.Model.Rol;
import com.gobierno.servicio_autorizacion.Ports.Output.RolRepository;

// Caso de uso para crear un nuevo rol en el sistema
@Service
public class CrearRolUseCase {

    // Repositorio para manejar la persistencia de los roles
    private final RolRepository rolRepository;

    // Constructor que inyecta el repositorio de roles
    public CrearRolUseCase(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    // Método que ejecuta la lógica para crear un nuevo rol a partir de un tipo de rol dado
    public Rol ejecutar(String tipoRol) {
        Rol rol = RolFactory.crearRol(tipoRol);
        return rolRepository.guardar(rol);
    }
    
}
