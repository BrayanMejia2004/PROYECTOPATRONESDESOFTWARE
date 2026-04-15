package com.gobierno.servicio_autorizacion.domain.ports.out;

import java.util.List;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public interface UsuariosRolesPort {

    void asignarRol(String username, Rol rol);

    void quitarRol(String username, Rol rol);

    List<String> listarPorUsername(String username);

    boolean tieneRol(String username, Rol rol);

    void eliminarPorUsername(String username);
    
    void eliminarPorRol(Rol rol);
}
