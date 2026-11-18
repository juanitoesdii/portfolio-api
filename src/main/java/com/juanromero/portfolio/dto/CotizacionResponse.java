
package com.juanromero.portfolio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.juanromero.portfolio.entity.EstadoCotizacion;

public record CotizacionResponse(

        Long id,
        Long proyectoId,
        String nombreProyecto,
        Integer numeroVersion,
        BigDecimal valor,
        LocalDate fecha,
        String descripcion,
        EstadoCotizacion estado

) {
}
