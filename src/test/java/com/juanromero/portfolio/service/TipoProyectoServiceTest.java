
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

import com.juanromero.portfolio.dto.TipoProyectoRequest;
import com.juanromero.portfolio.entity.TipoProyecto;
import com.juanromero.portfolio.exception.RecursoNoEncontradoException;
import com.juanromero.portfolio.repository.ProyectoRepository;
import com.juanromero.portfolio.repository.TipoProyectoRepository;

class TipoProyectoServiceTest {

    private TipoProyectoRepository tipoProyectoRepository;
    private ProyectoRepository proyectoRepository;
    private TipoProyectoService tipoProyectoService;

    @BeforeEach
    void configurar() {

        tipoProyectoRepository = mock(TipoProyectoRepository.class);
        proyectoRepository = mock(ProyectoRepository.class);

        tipoProyectoService = new TipoProyectoService(
                tipoProyectoRepository,
                proyectoRepository
        );
    }

    @Test
void debeCrearTipoProyectoCorrectamente() {

    TipoProyectoRequest request = new TipoProyectoRequest(
            "  Vivienda  ",
            "  Diseño de viviendas  "
    );

    when(tipoProyectoRepository.existsByNombreIgnoreCase("Vivienda"))
            .thenReturn(false);

    when(tipoProyectoRepository.save(any(TipoProyecto.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    var respuesta = tipoProyectoService.crearTipoProyecto(request);

    assertEquals("Vivienda", respuesta.nombre());
    assertEquals("Diseño de viviendas", respuesta.descripcion());

    verify(tipoProyectoRepository).save(any(TipoProyecto.class));
}

    @Test
    void noDebeCrearTipoProyectoConNombreDuplicado() {

        TipoProyectoRequest request = new TipoProyectoRequest(
                "Vivienda",
                "Diseño de viviendas"
        );

        when(tipoProyectoRepository.existsByNombreIgnoreCase("Vivienda"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> tipoProyectoService.crearTipoProyecto(request)
        );

        verify(tipoProyectoRepository, never()).save(any(TipoProyecto.class));
    }

    @Test
    void noDebeEliminarTipoProyectoQueEstaEnUso() {

        Long tipoId = 1L;
        TipoProyecto tipo = new TipoProyecto();

        when(tipoProyectoRepository.findById(tipoId))
                .thenReturn(Optional.of(tipo));

        when(proyectoRepository.existsByTipoProyectoId(tipoId))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> tipoProyectoService.eliminarTipoProyecto(tipoId)
        );

        verify(tipoProyectoRepository, never()).delete(any(TipoProyecto.class));
    }

    @Test
    void debeEliminarTipoProyectoSinProyectosAsociados() {

        Long tipoId = 1L;
        TipoProyecto tipo = new TipoProyecto();

        when(tipoProyectoRepository.findById(tipoId))
                .thenReturn(Optional.of(tipo));

        when(proyectoRepository.existsByTipoProyectoId(tipoId))
                .thenReturn(false);

        assertDoesNotThrow(
                () -> tipoProyectoService.eliminarTipoProyecto(tipoId)
        );

        verify(tipoProyectoRepository).delete(tipo);
    }

    @Test
    void debeLanzarErrorCuandoTipoProyectoNoExiste() {

        Long tipoId = 99L;

        when(tipoProyectoRepository.findById(tipoId))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> tipoProyectoService.eliminarTipoProyecto(tipoId)
        );

        verify(proyectoRepository, never()).existsByTipoProyectoId(anyLong());
        verify(tipoProyectoRepository, never()).delete(any(TipoProyecto.class));
    }
}
