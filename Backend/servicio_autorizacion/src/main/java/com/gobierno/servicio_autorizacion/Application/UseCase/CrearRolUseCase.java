package com.gobierno.servicio_autorizacion.Application.UseCase;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.Domain.Factory.AbstractRolCreator;
import com.gobierno.servicio_autorizacion.Domain.Model.Rol;
import com.gobierno.servicio_autorizacion.Ports.Output.RolRepository;

@Service
public class CrearRolUseCase {

    private final RolRepository rolRepository;
    private final Map<String, AbstractRolCreator> creators;

    public CrearRolUseCase(RolRepository rolRepository,
            Map<String, AbstractRolCreator> creators) {
        this.rolRepository = rolRepository;
        this.creators = creators;
    }

    public Rol ejecutar(String tipoRol) {
        AbstractRolCreator creator = creators.get(tipoRol.toUpperCase());
        if (creator == null) {
            throw new IllegalArgumentException("Tipo de rol invalido");
        }
        Rol rol = creator.crearRol();
        return rolRepository.guardar(rol);
    }
    
}
