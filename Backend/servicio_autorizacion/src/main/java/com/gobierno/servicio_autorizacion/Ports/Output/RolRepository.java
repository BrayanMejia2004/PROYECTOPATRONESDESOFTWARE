package com.gobierno.servicio_autorizacion.Ports.Output;

import com.gobierno.servicio_autorizacion.Domain.Model.Rol;

//Repositorio de roles, encargado de guardar los roles en la base de datos.
public interface RolRepository {
    
    Rol guardar(Rol rol);
}
