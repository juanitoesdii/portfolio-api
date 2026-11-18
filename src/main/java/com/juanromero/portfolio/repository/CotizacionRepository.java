
package com.juanromero.portfolio.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.juanromero.portfolio.entity.Cotizacion;
import com.juanromero.portfolio.entity.EstadoCotizacion;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    List<Cotizacion> findByProyectoIdOrderByNumeroVersionDesc(Long proyectoId);

    Optional<Cotizacion> findTopByProyectoIdOrderByNumeroVersionDesc(Long proyectoId);

    List<Cotizacion> findByProyectoIdAndEstadoAndIdNot(
        Long proyectoId,
        EstadoCotizacion estado,
        Long id
);

    long countByEstado(EstadoCotizacion estado);

    boolean existsByProyectoId(Long proyectoId);

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM Cotizacion c WHERE c.estado = :estado")
BigDecimal sumarValorPorEstado(@Param("estado") EstadoCotizacion estado);
}
