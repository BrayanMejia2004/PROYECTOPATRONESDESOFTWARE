package com.gobierno.servicio_autorizacion.domain.ports.out;

import java.util.List;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public interface RolesPermisosPort {

    void asignarPermiso(Rol rol, Permiso permiso);

    void quitarPermiso(Rol rol, Permiso permiso);

    List<Permiso> listarPorRol(Rol rol);

    List<Rol> listarRolesPorPermiso(Permiso permiso);

    boolean existeAsignacion(Rol rol, Permiso permiso);

    void eliminarPorRol(Rol rol);
}
