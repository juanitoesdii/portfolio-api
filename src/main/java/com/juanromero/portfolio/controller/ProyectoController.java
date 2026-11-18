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

import com.juanromero.portfolio.dto.ProyectoRequest;
import com.juanromero.portfolio.dto.ProyectoResponse;
import com.juanromero.portfolio.service.ProyectoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @PostMapping
    public ResponseEntity<ProyectoResponse> crearProyecto(
            @Valid @RequestBody ProyectoRequest request) {

        ProyectoResponse proyecto = proyectoService.crearProyecto(request);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(proyecto.id())
                .toUri();

        return ResponseEntity.created(ubicacion).body(proyecto);
    }

    @GetMapping
    public ResponseEntity<List<ProyectoResponse>> listarProyectos() {

        return ResponseEntity.ok(proyectoService.listarProyectos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponse> buscarProyectoPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                proyectoService.buscarProyectoPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProyectoResponse> actualizarProyecto(
            @PathVariable Long id,
            @Valid @RequestBody ProyectoRequest request) {

        return ResponseEntity.ok(
                proyectoService.actualizarProyecto(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProyecto(@PathVariable Long id) {

        proyectoService.eliminarProyecto(id);

        return ResponseEntity.noContent().build();
    }
}