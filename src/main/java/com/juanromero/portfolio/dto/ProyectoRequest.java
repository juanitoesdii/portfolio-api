package com.juanromero.portfolio.dto;

import java.time.LocalDate;

import com.juanromero.portfolio.entity.EstadoProyecto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProyectoRequest(

        @NotBlank(message = "El nombre del proyecto es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String nombre,

        @Size(max = 250, message = "La ubicación no puede superar 250 caracteres")
        String ubicacion,

        @NotNull(message = "Debes seleccionar un cliente")
        Long clienteId,

        @NotNull(message = "Debes seleccionar un tipo de proyecto")
        Long tipoProyectoId,

        @NotNull(message = "Debes seleccionar un tipo de servicio")
        Long tipoServicioId,

        LocalDate fechaPrevistaInicio,

        LocalDate fechaPrevistaFinalizacion,

        LocalDate fechaRealInicio,

        LocalDate fechaRealFinalizacion,

        EstadoProyecto estado

) {
}