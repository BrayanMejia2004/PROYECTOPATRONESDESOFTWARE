package com.gobierno.servicio_autorizacion.domain.ports.out;

import java.util.List;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public interface UsuariosRolesPort { // Puerto de salida para gestión de roles de usuarios

    void asignarRol(String username, Rol rol); // Asigna un rol a un usuario

    void quitarRol(String username, Rol rol); // Quita un rol a un usuario

    List<String> listarPorUsername(String username); // Lista los roles de un usuario por su username

    boolean tieneRol(String username, Rol rol); // Verifica si un usuario tiene un rol específico

    void eliminarPorUsername(String username); // Elimina todos los roles de un usuario

    void eliminarPorRol(Rol rol); // Elimina un rol específico de todos los usuarios
}