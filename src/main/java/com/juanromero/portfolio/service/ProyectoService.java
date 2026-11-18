
package com.juanromero.portfolio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juanromero.portfolio.dto.ProyectoRequest;
import com.juanromero.portfolio.dto.ProyectoResponse;
import com.juanromero.portfolio.entity.Cliente;
import com.juanromero.portfolio.entity.Proyecto;
import com.juanromero.portfolio.entity.TipoProyecto;
import com.juanromero.portfolio.entity.TipoServicio;
import com.juanromero.portfolio.exception.RecursoNoEncontradoException;
import com.juanromero.portfolio.exception.ReglaNegocioException;
import com.juanromero.portfolio.repository.ClienteRepository;
import com.juanromero.portfolio.repository.CotizacionRepository;
import com.juanromero.portfolio.repository.ProyectoRepository;
import com.juanromero.portfolio.repository.TipoProyectoRepository;
import com.juanromero.portfolio.repository.TipoServicioRepository;

@Service
@Transactional
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final ClienteRepository clienteRepository;
    private final TipoProyectoRepository tipoProyectoRepository;
    private final TipoServicioRepository tipoServicioRepository;
    private final CotizacionRepository cotizacionRepository;

    public ProyectoService(
            ProyectoRepository proyectoRepository,
            ClienteRepository clienteRepository,
            TipoProyectoRepository tipoProyectoRepository,
            TipoServicioRepository tipoServicioRepository,
        CotizacionRepository cotizacionRepository) {

        this.proyectoRepository = proyectoRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.clienteRepository = clienteRepository;
        this.tipoProyectoRepository = tipoProyectoRepository;
        this.tipoServicioRepository = tipoServicioRepository;
    }

    public ProyectoResponse crearProyecto(ProyectoRequest request) {

        validarFechas(request);

        Proyecto proyecto = new Proyecto();

        asignarDatos(proyecto, request);

        Proyecto guardado = proyectoRepository.save(proyecto);

        return convertirAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponse> listarProyectos() {

        return proyectoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProyectoResponse buscarProyectoPorId(Long id) {

        Proyecto proyecto = obtenerProyecto(id);

        return convertirAResponse(proyecto);
    }

    public ProyectoResponse actualizarProyecto(
            Long id,
            ProyectoRequest request) {

        validarFechas(request);

        Proyecto proyecto = obtenerProyecto(id);

        asignarDatos(proyecto, request);

        Proyecto actualizado = proyectoRepository.save(proyecto);

        return convertirAResponse(actualizado);
    }

    public void eliminarProyecto(Long id) {

    Proyecto proyecto = obtenerProyecto(id);

    if (cotizacionRepository.existsByProyectoId(id)) {
        throw new IllegalArgumentException(
                "No se puede eliminar el proyecto porque tiene cotizaciones asociadas"
        );
    }

    proyectoRepository.delete(proyecto);
}

    private void asignarDatos(
            Proyecto proyecto,
            ProyectoRequest request) {

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el cliente con ID: " + request.clienteId()
                ));

        TipoProyecto tipoProyecto = tipoProyectoRepository
                .findById(request.tipoProyectoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el tipo de proyecto con ID: "
                                + request.tipoProyectoId()
                ));

        TipoServicio tipoServicio = tipoServicioRepository
                .findById(request.tipoServicioId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el tipo de servicio con ID: "
                                + request.tipoServicioId()
                ));

        proyecto.setNombre(request.nombre().trim());
        proyecto.setUbicacion(normalizarTexto(request.ubicacion()));

        proyecto.setCliente(cliente);
        proyecto.setTipoProyecto(tipoProyecto);
        proyecto.setTipoServicio(tipoServicio);

        proyecto.setFechaPrevistaInicio(request.fechaPrevistaInicio());
        proyecto.setFechaPrevistaFinalizacion(
                request.fechaPrevistaFinalizacion()
        );

        proyecto.setFechaRealInicio(request.fechaRealInicio());
        proyecto.setFechaRealFinalizacion(
                request.fechaRealFinalizacion()
        );

        if (request.estado() != null) {
            proyecto.setEstado(request.estado());
        }
    }

    private void validarFechas(ProyectoRequest request) {

        if (request.fechaPrevistaInicio() != null
                && request.fechaPrevistaFinalizacion() != null
                && request.fechaPrevistaFinalizacion()
                        .isBefore(request.fechaPrevistaInicio())) {

            throw new ReglaNegocioException(
                    "La fecha prevista de finalización no puede ser anterior "
                            + "a la fecha prevista de inicio"
            );
        }

        if (request.fechaRealInicio() != null
                && request.fechaRealFinalizacion() != null
                && request.fechaRealFinalizacion()
                        .isBefore(request.fechaRealInicio())) {

            throw new ReglaNegocioException(
                    "La fecha real de finalización no puede ser anterior "
                            + "a la fecha real de inicio"
            );
        }

        if (request.fechaRealFinalizacion() != null
                && request.fechaRealInicio() == null) {

            throw new ReglaNegocioException(
                    "No puedes registrar una fecha real de finalización "
                            + "sin una fecha real de inicio"
            );
        }
    }

    private Proyecto obtenerProyecto(Long id) {

        return proyectoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el proyecto con ID: " + id
                ));
    }

    private ProyectoResponse convertirAResponse(Proyecto proyecto) {

        return new ProyectoResponse(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getUbicacion(),

                proyecto.getCliente().getId(),
                proyecto.getCliente().getNombre(),

                proyecto.getTipoProyecto().getId(),
                proyecto.getTipoProyecto().getNombre(),

                proyecto.getTipoServicio().getId(),
                proyecto.getTipoServicio().getNombre(),

                proyecto.getFechaPrevistaInicio(),
                proyecto.getFechaPrevistaFinalizacion(),

                proyecto.getFechaRealInicio(),
                proyecto.getFechaRealFinalizacion(),

                proyecto.getEstado()
        );
    }

    private String normalizarTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}
