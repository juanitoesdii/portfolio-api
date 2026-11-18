package com.juanromero.portfolio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juanromero.portfolio.dto.TipoServicioRequest;
import com.juanromero.portfolio.dto.TipoServicioResponse;
import com.juanromero.portfolio.entity.TipoServicio;
import com.juanromero.portfolio.exception.RecursoNoEncontradoException;
import com.juanromero.portfolio.repository.ProyectoRepository;
import com.juanromero.portfolio.repository.TipoServicioRepository;

@Service
@Transactional
public class TipoServicioService {

    private final TipoServicioRepository tipoServicioRepository;
    private final ProyectoRepository proyectoRepository;

    public TipoServicioService(TipoServicioRepository tipoServicioRepository, ProyectoRepository proyectoRepository) {
        this.tipoServicioRepository = tipoServicioRepository;
        this.proyectoRepository = proyectoRepository;
    }

    public TipoServicioResponse crearTipoServicio(TipoServicioRequest request) {

        String nombre = request.nombre().trim();

        if (tipoServicioRepository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException(
                    "Ya existe un tipo de servicio con el nombre: " + nombre
            );
        }

        TipoServicio tipoServicio = new TipoServicio();
        tipoServicio.setNombre(nombre);
        tipoServicio.setDescripcion(normalizarTexto(request.descripcion()));

        TipoServicio guardado = tipoServicioRepository.save(tipoServicio);

        return convertirAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<TipoServicioResponse> listarTiposServicio() {

        return tipoServicioRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TipoServicioResponse buscarTipoServicioPorId(Long id) {

        TipoServicio tipoServicio = obtenerTipoServicio(id);

        return convertirAResponse(tipoServicio);
    }

    public TipoServicioResponse actualizarTipoServicio(
            Long id,
            TipoServicioRequest request) {

        TipoServicio tipoServicio = obtenerTipoServicio(id);
        String nombre = request.nombre().trim();

        boolean nombreDuplicado = tipoServicioRepository
                .existsByNombreIgnoreCase(nombre);

        if (nombreDuplicado && !tipoServicio.getNombre().equalsIgnoreCase(nombre)) {
            throw new IllegalArgumentException(
                    "Ya existe un tipo de servicio con el nombre: " + nombre
            );
        }

        tipoServicio.setNombre(nombre);
        tipoServicio.setDescripcion(normalizarTexto(request.descripcion()));

        TipoServicio actualizado = tipoServicioRepository.save(tipoServicio);

        return convertirAResponse(actualizado);
    }

    public void eliminarTipoServicio(Long id) {

    TipoServicio tipoServicio = obtenerTipoServicio(id);

    if (proyectoRepository.existsByTipoServicioId(id)) {
        throw new IllegalArgumentException(
                "No se puede eliminar el tipo de servicio porque está siendo utilizado"
        );
    }

    tipoServicioRepository.delete(tipoServicio);
}

    private TipoServicio obtenerTipoServicio(Long id) {

        return tipoServicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el tipo de servicio con ID: " + id
                ));
    }

    private TipoServicioResponse convertirAResponse(TipoServicio tipoServicio) {

        return new TipoServicioResponse(
                tipoServicio.getId(),
                tipoServicio.getNombre(),
                tipoServicio.getDescripcion()
        );
    }

    private String normalizarTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}