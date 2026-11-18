
package com.juanromero.portfolio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juanromero.portfolio.dto.CotizacionRequest;
import com.juanromero.portfolio.dto.CotizacionResponse;
import com.juanromero.portfolio.entity.Cotizacion;
import com.juanromero.portfolio.entity.EstadoCotizacion;
import com.juanromero.portfolio.entity.Proyecto;
import com.juanromero.portfolio.exception.RecursoNoEncontradoException;
import com.juanromero.portfolio.repository.CotizacionRepository;
import com.juanromero.portfolio.repository.ProyectoRepository;

@Service
@Transactional
public class CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final ProyectoRepository proyectoRepository;

    public CotizacionService(
            CotizacionRepository cotizacionRepository,
            ProyectoRepository proyectoRepository) {

        this.cotizacionRepository = cotizacionRepository;
        this.proyectoRepository = proyectoRepository;
    }

    public CotizacionResponse crearCotizacion(CotizacionRequest request) {

        Proyecto proyecto = proyectoRepository.findById(request.proyectoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el proyecto con ID: " + request.proyectoId()
                ));

        Integer siguienteVersion = cotizacionRepository
                .findTopByProyectoIdOrderByNumeroVersionDesc(proyecto.getId())
                .map(cotizacion -> cotizacion.getNumeroVersion() + 1)
                .orElse(1);

        Cotizacion cotizacion = new Cotizacion();

        cotizacion.setProyecto(proyecto);
        cotizacion.setNumeroVersion(siguienteVersion);
        cotizacion.setValor(request.valor());
        cotizacion.setFecha(request.fecha());
        cotizacion.setDescripcion(request.descripcion().trim());
        cotizacion.setEstado(EstadoCotizacion.PENDIENTE);

        Cotizacion guardada = cotizacionRepository.save(cotizacion);

        return convertirAResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<CotizacionResponse> listarCotizacionesPorProyecto(Long proyectoId) {

        if (!proyectoRepository.existsById(proyectoId)) {
            throw new RecursoNoEncontradoException(
                    "No se encontró el proyecto con ID: " + proyectoId
            );
        }

        return cotizacionRepository
                .findByProyectoIdOrderByNumeroVersionDesc(proyectoId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CotizacionResponse buscarCotizacionPorId(Long id) {

        Cotizacion cotizacion = obtenerCotizacion(id);

        return convertirAResponse(cotizacion);
    }

    public CotizacionResponse actualizarEstado(
            Long id,
            EstadoCotizacion nuevoEstado) {

        Cotizacion cotizacion = obtenerCotizacion(id);

        if (nuevoEstado == EstadoCotizacion.ACEPTADA) {

            Long proyectoId = cotizacion.getProyecto().getId();

            Proyecto proyecto = proyectoRepository.findByIdForUpdate(proyectoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró el proyecto con ID: " + proyectoId
                    ));

            List<Cotizacion> cotizacionesAceptadas =
                    cotizacionRepository.findByProyectoIdAndEstadoAndIdNot(
                            proyecto.getId(),
                            EstadoCotizacion.ACEPTADA,
                            cotizacion.getId()
                    );

            for (Cotizacion otraCotizacion : cotizacionesAceptadas) {
                otraCotizacion.setEstado(EstadoCotizacion.PENDIENTE);
            }

            cotizacionRepository.saveAll(cotizacionesAceptadas);
        }

        cotizacion.setEstado(nuevoEstado);

        Cotizacion actualizada = cotizacionRepository.save(cotizacion);

        return convertirAResponse(actualizada);
    }

    private Cotizacion obtenerCotizacion(Long id) {

        return cotizacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró la cotización con ID: " + id
                ));
    }

    private CotizacionResponse convertirAResponse(Cotizacion cotizacion) {

        return new CotizacionResponse(
                cotizacion.getId(),
                cotizacion.getProyecto().getId(),
                cotizacion.getProyecto().getNombre(),
                cotizacion.getNumeroVersion(),
                cotizacion.getValor(),
                cotizacion.getFecha(),
                cotizacion.getDescripcion(),
                cotizacion.getEstado()
        );
    }
}
