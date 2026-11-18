package com.juanromero.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juanromero.portfolio.entity.TipoServicio;

public interface TipoServicioRepository extends JpaRepository<TipoServicio, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}