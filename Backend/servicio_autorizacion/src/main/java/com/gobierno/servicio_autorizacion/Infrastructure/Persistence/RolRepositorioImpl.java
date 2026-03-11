package com.gobierno.servicio_autorizacion.Infrastructure.Persistence;

import org.springframework.stereotype.Repository;

import com.gobierno.servicio_autorizacion.Domain.Model.Rol;
import com.gobierno.servicio_autorizacion.Ports.Output.RolRepository;

//Implementación del repositorio de roles
@Repository
public class RolRepositorioImpl implements RolRepository {

    private final RolJpaRepository rolJpaRepository;

    public RolRepositorioImpl(RolJpaRepository rolJpaRepository) {
        this.rolJpaRepository = rolJpaRepository;
    }

    // Metodo encargado de guardar los roles en la base de datos.
    @Override
    public Rol guardar(Rol rol) {
        return rolJpaRepository.save(rol);
    }

}
