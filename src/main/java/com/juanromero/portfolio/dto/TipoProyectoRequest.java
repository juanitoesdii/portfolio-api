package com.juanromero.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoProyectoRequest(

        @NotBlank(message = "El nombre del tipo de proyecto es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Size(max = 250, message = "La descripción no puede superar 250 caracteres")
        String descripcion

) {
}