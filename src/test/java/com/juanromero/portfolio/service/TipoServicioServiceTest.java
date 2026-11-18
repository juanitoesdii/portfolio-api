
package com.juanromero.portfolio.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.juanromero.portfolio.dto.TipoServicioRequest;
import com.juanromero.portfolio.entity.TipoServicio;
import com.juanromero.portfolio.exception.RecursoNoEncontradoException;
import com.juanromero.portfolio.repository.ProyectoRepository;
import com.juanromero.portfolio.repository.TipoServicioRepository;

class TipoServicioServiceTest {

    private TipoServicioRepository tipoServicioRepository;
    private ProyectoRepository proyectoRepository;
    private TipoServicioService tipoServicioService;

    @BeforeEach
    void configurar() {

        tipoServicioRepository = mock(TipoServicioRepository.class);
        proyectoRepository = mock(ProyectoRepository.class);

        tipoServicioService = new TipoServicioService(
                tipoServicioRepository,
                proyectoRepository
        );
    }

    @Test
    void debeCrearTipoServicioCorrectamente() {

        TipoServicioRequest request = new TipoServicioRequest(
                "  Diseño arquitectónico  ",
                "  Diseño y planificación de espacios  "
        );

        when(tipoServicioRepository.existsByNombreIgnoreCase("Diseño arquitectónico"))
                .thenReturn(false);

        when(tipoServicioRepository.save(any(TipoServicio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = tipoServicioService.crearTipoServicio(request);

        assertEquals("Diseño arquitectónico", respuesta.nombre());
        assertEquals("Diseño y planificación de espacios", respuesta.descripcion());

        verify(tipoServicioRepository).save(any(TipoServicio.class));
    }

    @Test
    void noDebeCrearTipoServicioConNombreDuplicado() {

        TipoServicioRequest request = new TipoServicioRequest(
                "Diseño arquitectónico",
                "Diseño de espacios"
        );

        when(tipoServicioRepository.existsByNombreIgnoreCase("Diseño arquitectónico"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> tipoServicioService.crearTipoServicio(request)
        );

        verify(tipoServicioRepository, never()).save(any(TipoServicio.class));
    }

    @Test
    void noDebeEliminarTipoServicioQueEstaEnUso() {

        Long tipoId = 1L;
        TipoServicio tipo = new TipoServicio();

        when(tipoServicioRepository.findById(tipoId))
                .thenReturn(Optional.of(tipo));

        when(proyectoRepository.existsByTipoServicioId(tipoId))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> tipoServicioService.eliminarTipoServicio(tipoId)
        );

        verify(tipoServicioRepository, never()).delete(any(TipoServicio.class));
    }

    @Test
    void debeEliminarTipoServicioSinProyectosAsociados() {

        Long tipoId = 1L;
        TipoServicio tipo = new TipoServicio();

        when(tipoServicioRepository.findById(tipoId))
                .thenReturn(Optional.of(tipo));

        when(proyectoRepository.existsByTipoServicioId(tipoId))
                .thenReturn(false);

        assertDoesNotThrow(
                () -> tipoServicioService.eliminarTipoServicio(tipoId)
        );

        verify(tipoServicioRepository).delete(tipo);
    }

    @Test
    void debeLanzarErrorCuandoTipoServicioNoExiste() {

        Long tipoId = 99L;

        when(tipoServicioRepository.findById(tipoId))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> tipoServicioService.eliminarTipoServicio(tipoId)
        );

        verify(proyectoRepository, never()).existsByTipoServicioId(anyLong());
        verify(tipoServicioRepository, never()).delete(any(TipoServicio.class));
    }
}
