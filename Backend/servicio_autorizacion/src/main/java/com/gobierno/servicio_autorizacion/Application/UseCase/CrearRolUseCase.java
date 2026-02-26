package com.gobierno.servicio_autorizacion.Application.UseCase;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.Domain.Model.Rol;
import com.gobierno.servicio_autorizacion.Domain.Model.RolFactory;

@Service
public class CrearRolUseCase {

    public Rol ejecutar(String TipoRol) {
        
        Rol rol = RolFactory.crearRol(TipoRol);
        return rol;
    }
    
}
