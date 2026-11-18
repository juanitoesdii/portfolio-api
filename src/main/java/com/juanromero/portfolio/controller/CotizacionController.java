
package com.juanromero.portfolio.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.juanromero.portfolio.dto.CotizacionRequest;
import com.juanromero.portfolio.dto.CotizacionResponse;
import com.juanromero.portfolio.dto.EstadoCotizacionRequest;
import com.juanromero.portfolio.service.CotizacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    public CotizacionController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> crearCotizacion(
            @Valid @RequestBody CotizacionRequest request) {

        CotizacionResponse cotizacion =
                cotizacionService.crearCotizacion(request);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cotizacion.id())
                .toUri();

        return ResponseEntity.created(ubicacion).body(cotizacion);
    }

    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<CotizacionResponse>> listarPorProyecto(
            @PathVariable Long proyectoId) {

        return ResponseEntity.ok(
                cotizacionService.listarCotizacionesPorProyecto(proyectoId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotizacionResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cotizacionService.buscarCotizacionPorId(id)
        );
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CotizacionResponse> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoCotizacionRequest request) {

        return ResponseEntity.ok(
                cotizacionService.actualizarEstado(id, request.estado())
        );
    }
}
