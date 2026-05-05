package com.gobierno.servicio_autorizacion.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.ports.out.PermisoRepositoryPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.PermisoJpaRepository;

@Repository
public class PermisoRepositorioAdapter implements PermisoRepositoryPort { // Adapter que implementa el puerto del
                                                                          // dominio

    private final PermisoJpaRepository permisoJpaRepository; // Repositorio JPA

    public PermisoRepositorioAdapter(PermisoJpaRepository permisoJpaRepository) {
        this.permisoJpaRepository = permisoJpaRepository;
    }

    @Override
    @Transactional
    public Permiso guardar(Permiso permiso) { // Persiste un permiso
        return permisoJpaRepository.save(permiso);
    }

    @Override
    public Optional<Permiso> buscarPorId(Long id) { // Busca un permiso por ID
        return permisoJpaRepository.findById(id);
    }

    @Override
    public Optional<Permiso> buscarPorNombre(String nombre) { // Busca un permiso por nombre
        return permisoJpaRepository.findByNombre(nombre);
    }

    @Override
    public List<Permiso> listarTodos() { // Lista todos los permisos
        return permisoJpaRepository.findAll();
    }

    @Override
    public boolean existePorNombre(String nombre) { // Verifica si existe un permiso por nombre
        return permisoJpaRepository.existsByNombre(nombre);
    }
}