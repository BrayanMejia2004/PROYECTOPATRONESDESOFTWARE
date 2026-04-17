package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;

@Service
public class CrearRolConPermisosUseCase {  // Caso de uso para crear un rol con permisos

    private final CrearRolUseCase crearRolUseCase;  // Caso de uso para crear roles
    private final AsignarPermisosARolUseCase asignarPermisosUseCase;  // Caso de uso para asignar permisos

    public CrearRolConPermisosUseCase(CrearRolUseCase crearRolUseCase,
                                      AsignarPermisosARolUseCase asignarPermisosUseCase) {
        this.crearRolUseCase = crearRolUseCase;
        this.asignarPermisosUseCase = asignarPermisosUseCase;
    }

    @Transactional  // Método transaccional
    public Rol ejecutar(String nombreRol, String descripcion, List<String> permisos) {  // Método principal
        Rol rol = crearRolUseCase.ejecutar(nombreRol, descripcion);  // Crea el rol

        if (permisos != null && !permisos.isEmpty()) {  // Si se especificaron permisos
            asignarPermisosUseCase.ejecutar(nombreRol, permisos);  // Asigna los permisos al rol
        }

        return rol;  // Retorna el rol creado
    }
}