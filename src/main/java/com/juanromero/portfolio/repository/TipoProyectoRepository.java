package com.juanromero.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juanromero.portfolio.entity.TipoProyecto;

public interface TipoProyectoRepository extends JpaRepository<TipoProyecto, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}