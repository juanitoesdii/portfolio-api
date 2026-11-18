
package com.juanromero.portfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.juanromero.portfolio.entity.EstadoProyecto;
import com.juanromero.portfolio.entity.Proyecto;

import jakarta.persistence.LockModeType;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    long countByEstado(EstadoProyecto estado);
    boolean existsByClienteId(Long clienteId);
    boolean existsByTipoProyectoId(Long tipoProyectoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Proyecto p WHERE p.id = :id")
Optional<Proyecto> findByIdForUpdate(@Param("id") Long id);

boolean existsByTipoServicioId(Long tipoServicioId);
}
