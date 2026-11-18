
package com.juanromero.portfolio.dto;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardResponse(

        long totalClientes,
        long totalProyectos,
        Map<String, Long> proyectosPorEstado,
        long totalCotizaciones,
        Map<String, Long> cotizacionesPorEstado,
        BigDecimal valorCotizacionesAceptadas

) {
}
