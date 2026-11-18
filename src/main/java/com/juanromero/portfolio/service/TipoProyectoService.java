package com.juanromero.portfolio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juanromero.portfolio.dto.TipoProyectoRequest;
import com.juanromero.portfolio.dto.TipoProyectoResponse;
import com.juanromero.portfolio.entity.TipoProyecto;
import com.juanromero.portfolio.exception.RecursoNoEncontradoException;
import com.juanromero.portfolio.repository.ProyectoRepository;
import com.juanromero.portfolio.repository.TipoProyectoRepository;

@Service
@Transactional
public class TipoProyectoService {

    private final TipoProyectoRepository tipoProyectoRepository;
    private final ProyectoRepository proyectoRepository;

    public TipoProyectoService(TipoProyectoRepository tipoProyectoRepository, ProyectoRepository proyectoRepository) {
        this.tipoProyectoRepository = tipoProyectoRepository;
        this.proyectoRepository = proyectoRepository;
    }

    public TipoProyectoResponse crearTipoProyecto(TipoProyectoRequest request) {

        String nombre = request.nombre().trim();

        if (tipoProyectoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException(
                    "Ya existe un tipo de proyecto con el nombre: " + nombre
            );
        }

        TipoProyecto tipoProyecto = new TipoProyecto();
        tipoProyecto.setNombre(nombre);
        tipoProyecto.setDescripcion(normalizarTexto(request.descripcion()));

        TipoProyecto guardado = tipoProyectoRepository.save(tipoProyecto);

        return convertirAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<TipoProyectoResponse> listarTiposProyecto() {

        return tipoProyectoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TipoProyectoResponse buscarTipoProyectoPorId(Long id) {

        TipoProyecto tipoProyecto = obtenerTipoProyecto(id);

        return convertirAResponse(tipoProyecto);
    }

    public TipoProyectoResponse actualizarTipoProyecto(
            Long id,
            TipoProyectoRequest request) {

        TipoProyecto tipoProyecto = obtenerTipoProyecto(id);
        String nombre = request.nombre().trim();

        boolean nombreDuplicado = tipoProyectoRepository
                .existsByNombreIgnoreCase(nombre);

        if (nombreDuplicado && !tipoProyecto.getNombre().equalsIgnoreCase(nombre)) {
            throw new IllegalArgumentException(
                    "Ya existe un tipo de proyecto con el nombre: " + nombre
            );
        }

        tipoProyecto.setNombre(nombre);
        tipoProyecto.setDescripcion(normalizarTexto(request.descripcion()));

        TipoProyecto actualizado = tipoProyectoRepository.save(tipoProyecto);

        return convertirAResponse(actualizado);
    }

    public void eliminarTipoProyecto(Long id) {

    TipoProyecto tipoProyecto = obtenerTipoProyecto(id);

    if (proyectoRepository.existsByTipoProyectoId(id)) {
        throw new IllegalArgumentException(
                "No se puede eliminar el tipo de proyecto porque está siendo utilizado"
        );
    }

    tipoProyectoRepository.delete(tipoProyecto);
}

    private TipoProyecto obtenerTipoProyecto(Long id) {

        return tipoProyectoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el tipo de proyecto con ID: " + id
                ));
    }

    private TipoProyectoResponse convertirAResponse(TipoProyecto tipoProyecto) {

        return new TipoProyectoResponse(
                tipoProyecto.getId(),
                tipoProyecto.getNombre(),
                tipoProyecto.getDescripcion()
        );
    }

    private String normalizarTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}