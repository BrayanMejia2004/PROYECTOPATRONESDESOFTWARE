package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;

@Service
public class CrearRolConPermisosUseCase {

    private final CrearRolUseCase crearRolUseCase;
    private final AsignarPermisosARolUseCase asignarPermisosUseCase;

    public CrearRolConPermisosUseCase(CrearRolUseCase crearRolUseCase,
                                      AsignarPermisosARolUseCase asignarPermisosUseCase) {
        this.crearRolUseCase = crearRolUseCase;
        this.asignarPermisosUseCase = asignarPermisosUseCase;
    }

    @Transactional
    public Rol ejecutar(String nombreRol, String descripcion, List<String> permisos) {
        Rol rol = crearRolUseCase.ejecutar(nombreRol, descripcion);

        if (permisos != null && !permisos.isEmpty()) {
            asignarPermisosUseCase.ejecutar(nombreRol, permisos);
        }

        return rol;
    }
}
