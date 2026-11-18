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

import com.juanromero.portfolio.dto.TipoServicioRequest;
import com.juanromero.portfolio.dto.TipoServicioResponse;
import com.juanromero.portfolio.service.TipoServicioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tipos-servicio")
public class TipoServicioController {

    private final TipoServicioService tipoServicioService;

    public TipoServicioController(TipoServicioService tipoServicioService) {
        this.tipoServicioService = tipoServicioService;
    }

    @PostMapping
    public ResponseEntity<TipoServicioResponse> crearTipoServicio(
            @Valid @RequestBody TipoServicioRequest request) {

        TipoServicioResponse tipo = tipoServicioService.crearTipoServicio(request);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tipo.id())
                .toUri();

        return ResponseEntity.created(ubicacion).body(tipo);
    }

    @GetMapping
    public ResponseEntity<List<TipoServicioResponse>> listarTiposServicio() {

        return ResponseEntity.ok(tipoServicioService.listarTiposServicio());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoServicioResponse> buscarTipoServicioPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                tipoServicioService.buscarTipoServicioPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoServicioResponse> actualizarTipoServicio(
            @PathVariable Long id,
            @Valid @RequestBody TipoServicioRequest request) {

        return ResponseEntity.ok(
                tipoServicioService.actualizarTipoServicio(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipoServicio(@PathVariable Long id) {

        tipoServicioService.eliminarTipoServicio(id);

        return ResponseEntity.noContent().build();
    }
}