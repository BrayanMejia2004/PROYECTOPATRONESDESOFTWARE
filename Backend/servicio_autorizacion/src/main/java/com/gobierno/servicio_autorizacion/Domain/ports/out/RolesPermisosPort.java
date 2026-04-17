package com.gobierno.servicio_autorizacion.domain.ports.out;

import java.util.List;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public interface RolesPermisosPort { // Puerto de salida para gestión de permisos de roles

    void asignarPermiso(Rol rol, Permiso permiso); // Asigna un permiso a un rol

    void quitarPermiso(Rol rol, Permiso permiso); // Quita un permiso a un rol

    List<Permiso> listarPorRol(Rol rol); // Lista los permisos de un rol

    List<Rol> listarRolesPorPermiso(Permiso permiso); // Lista los roles que tienen un permiso específico

    boolean existeAsignacion(Rol rol, Permiso permiso); // Verifica si un rol tiene un permiso específico

    void eliminarPorRol(Rol rol); // Elimina todos los permisos de un rol
}