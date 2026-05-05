package com.gobierno.servicio_autorizacion.infrastructure.persistence;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolRepositoryPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@Repository
public class RolRepositorioAdapter implements RolRepositoryPort { // Adapter que implementa el puerto del dominio

    private final RolJpaRepository rolJpaRepository; // Repositorio JPA

    public RolRepositorioAdapter(RolJpaRepository rolJpaRepository) {
        this.rolJpaRepository = rolJpaRepository;
    }

    @Override
    @Transactional // Método transaccional
    public Rol guardar(Rol rol) { // Persiste un rol (create o update)
        return rolJpaRepository.save(rol);
    }

    @Override
    public Optional<Rol> findByNombre(String nombre) { // Busca un rol por nombre
        return rolJpaRepository.findByNombre(nombre);
    }
}