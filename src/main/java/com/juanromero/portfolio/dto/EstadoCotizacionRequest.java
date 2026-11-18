
package com.juanromero.portfolio.dto;

import com.juanromero.portfolio.entity.EstadoCotizacion;

import jakarta.validation.constraints.NotNull;

public record EstadoCotizacionRequest(

        @NotNull(message = "Debes indicar el nuevo estado")
        EstadoCotizacion estado

) {
}
