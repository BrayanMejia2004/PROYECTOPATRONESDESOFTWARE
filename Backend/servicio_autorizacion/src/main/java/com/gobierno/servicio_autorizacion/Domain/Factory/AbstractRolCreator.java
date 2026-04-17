package com.gobierno.servicio_autorizacion.domain.factory;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public abstract class AbstractRolCreator { // Clase abstracta (Creator) del patrón Factory Method

    public abstract Rol crearRol(); // Método abstracto para crear un rol (factory method)

}