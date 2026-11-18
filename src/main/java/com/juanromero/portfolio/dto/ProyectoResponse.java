package com.juanromero.portfolio.dto;

import java.time.LocalDate;

import com.juanromero.portfolio.entity.EstadoProyecto;

public record ProyectoResponse(

        Long id,
        String nombre,
        String ubicacion,

        Long clienteId,
        String nombreCliente,

        Long tipoProyectoId,
        String nombreTipoProyecto,

        Long tipoServicioId,
        String nombreTipoServicio,

        LocalDate fechaPrevistaInicio,
        LocalDate fechaPrevistaFinalizacion,

        LocalDate fechaRealInicio,
        LocalDate fechaRealFinalizacion,

        EstadoProyecto estado

) {
}