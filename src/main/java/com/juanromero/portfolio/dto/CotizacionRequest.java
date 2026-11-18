
package com.juanromero.portfolio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CotizacionRequest(

        @NotNull(message = "Debes seleccionar un proyecto")
        Long proyectoId,

        @NotNull(message = "El valor de la cotización es obligatorio")
        @DecimalMin(value = "0.01", message = "El valor debe ser mayor que cero")
        BigDecimal valor,

        @NotNull(message = "La fecha de la cotización es obligatoria")
        LocalDate fecha,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
        String descripcion

) {
}
