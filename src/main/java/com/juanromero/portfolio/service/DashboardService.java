
package com.juanromero.portfolio.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juanromero.portfolio.dto.DashboardResponse;
import com.juanromero.portfolio.entity.EstadoCotizacion;
import com.juanromero.portfolio.entity.EstadoProyecto;
import com.juanromero.portfolio.repository.ClienteRepository;
import com.juanromero.portfolio.repository.CotizacionRepository;
import com.juanromero.portfolio.repository.ProyectoRepository;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final ProyectoRepository proyectoRepository;
    private final CotizacionRepository cotizacionRepository;

    public DashboardService(
            ClienteRepository clienteRepository,
            ProyectoRepository proyectoRepository,
            CotizacionRepository cotizacionRepository) {

        this.clienteRepository = clienteRepository;
        this.proyectoRepository = proyectoRepository;
        this.cotizacionRepository = cotizacionRepository;
    }

    public DashboardResponse obtenerResumen() {

        long totalClientes = clienteRepository.count();

        long totalProyectos = proyectoRepository.count();

        Map<String, Long> proyectosPorEstado = new LinkedHashMap<>();

        for (EstadoProyecto estado : EstadoProyecto.values()) {
            proyectosPorEstado.put(
                    estado.name(),
                    proyectoRepository.countByEstado(estado)
            );
        }

        long totalCotizaciones = cotizacionRepository.count();

        Map<String, Long> cotizacionesPorEstado = new LinkedHashMap<>();

        for (EstadoCotizacion estado : EstadoCotizacion.values()) {
            cotizacionesPorEstado.put(
                    estado.name(),
                    cotizacionRepository.countByEstado(estado)
            );
        }

        BigDecimal valorCotizacionesAceptadas =
                cotizacionRepository.sumarValorPorEstado(
                        EstadoCotizacion.ACEPTADA
                );

        return new DashboardResponse(
                totalClientes,
                totalProyectos,
                proyectosPorEstado,
                totalCotizaciones,
                cotizacionesPorEstado,
                valorCotizacionesAceptadas
        );
    }
}
