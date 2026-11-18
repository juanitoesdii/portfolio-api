package com.juanromero.portfolio.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.juanromero.portfolio.dto.TipoProyectoRequest;
import com.juanromero.portfolio.dto.TipoProyectoResponse;
import com.juanromero.portfolio.service.TipoProyectoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tipos-proyecto")
public class TipoProyectoController {

    private final TipoProyectoService tipoProyectoService;

    public TipoProyectoController(TipoProyectoService tipoProyectoService) {
        this.tipoProyectoService = tipoProyectoService;
    }

    @PostMapping
    public ResponseEntity<TipoProyectoResponse> crearTipoProyecto(
            @Valid @RequestBody TipoProyectoRequest request) {

        TipoProyectoResponse tipo = tipoProyectoService.crearTipoProyecto(request);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tipo.id())
                .toUri();

        return ResponseEntity.created(ubicacion).body(tipo);
    }

    @GetMapping
    public ResponseEntity<List<TipoProyectoResponse>> listarTiposProyecto() {

        return ResponseEntity.ok(tipoProyectoService.listarTiposProyecto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoProyectoResponse> buscarTipoProyectoPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                tipoProyectoService.buscarTipoProyectoPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoProyectoResponse> actualizarTipoProyecto(
            @PathVariable Long id,
            @Valid @RequestBody TipoProyectoRequest request) {

        return ResponseEntity.ok(
                tipoProyectoService.actualizarTipoProyecto(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipoProyecto(@PathVariable Long id) {

        tipoProyectoService.eliminarTipoProyecto(id);

        return ResponseEntity.noContent().build();
    }
}