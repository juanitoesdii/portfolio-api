
package com.juanromero.portfolio.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.juanromero.portfolio.dto.CotizacionRequest;
import com.juanromero.portfolio.entity.Cotizacion;
import com.juanromero.portfolio.entity.EstadoCotizacion;
import com.juanromero.portfolio.entity.Proyecto;
import com.juanromero.portfolio.exception.RecursoNoEncontradoException;
import com.juanromero.portfolio.repository.CotizacionRepository;
import com.juanromero.portfolio.repository.ProyectoRepository;

class CotizacionServiceTest {

    private CotizacionRepository cotizacionRepository;
    private ProyectoRepository proyectoRepository;
    private CotizacionService cotizacionService;

    @BeforeEach
    void configurar() {

        cotizacionRepository = mock(CotizacionRepository.class);
        proyectoRepository = mock(ProyectoRepository.class);

        cotizacionService = new CotizacionService(
                cotizacionRepository,
                proyectoRepository
        );
    }

    private void asignarId(Object entidad, Long id) {

        try {
            Field campoId = entidad.getClass().getDeclaredField("id");
            campoId.setAccessible(true);
            campoId.set(entidad, id);

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(
                    "No se pudo asignar el ID de prueba", e
            );
        }
    }

    private Proyecto crearProyecto() {

        Proyecto proyecto = new Proyecto();

        asignarId(proyecto, 1L);
        proyecto.setNombre("Diseño de vivienda");

        return proyecto;
    }

    private Cotizacion crearCotizacion(
            Proyecto proyecto,
            int version,
            EstadoCotizacion estado) {

        Cotizacion cotizacion = new Cotizacion();

        asignarId(cotizacion, (long) version);

        cotizacion.setProyecto(proyecto);
        cotizacion.setNumeroVersion(version);
        cotizacion.setValor(new BigDecimal("50000000"));
        cotizacion.setFecha(LocalDate.of(2026, 10, 30));
        cotizacion.setDescripcion("Diseño arquitectónico");
        cotizacion.setEstado(estado);

        return cotizacion;
    }

    @Test
    void debeCrearPrimeraCotizacionConVersionUno() {

        Proyecto proyecto = crearProyecto();

        CotizacionRequest request = new CotizacionRequest(
                1L,
                new BigDecimal("50000000"),
                LocalDate.of(2026, 10, 30),
                "  Diseño arquitectónico  "
        );

        when(proyectoRepository.findById(1L))
                .thenReturn(Optional.of(proyecto));

        when(cotizacionRepository
                .findTopByProyectoIdOrderByNumeroVersionDesc(any()))
                .thenReturn(Optional.empty());

        when(cotizacionRepository.save(any(Cotizacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = cotizacionService.crearCotizacion(request);

        assertEquals(1, respuesta.numeroVersion());
        assertEquals("50000000", respuesta.valor().toPlainString());
        assertEquals("Diseño arquitectónico", respuesta.descripcion());
        assertEquals(EstadoCotizacion.PENDIENTE, respuesta.estado());
        assertEquals("Diseño de vivienda", respuesta.nombreProyecto());

        verify(cotizacionRepository).save(any(Cotizacion.class));
    }

    @Test
    void debeIncrementarVersionAlCrearNuevaCotizacion() {

        Proyecto proyecto = crearProyecto();

        Cotizacion anterior = crearCotizacion(
                proyecto,
                2,
                EstadoCotizacion.RECHAZADA
        );

        CotizacionRequest request = new CotizacionRequest(
                1L,
                new BigDecimal("60000000"),
                LocalDate.of(2026, 11, 1),
                "Nueva propuesta"
        );

        when(proyectoRepository.findById(1L))
                .thenReturn(Optional.of(proyecto));

        when(cotizacionRepository
                .findTopByProyectoIdOrderByNumeroVersionDesc(any()))
                .thenReturn(Optional.of(anterior));

        when(cotizacionRepository.save(any(Cotizacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = cotizacionService.crearCotizacion(request);

        assertEquals(3, respuesta.numeroVersion());
        assertEquals(EstadoCotizacion.PENDIENTE, respuesta.estado());

        verify(cotizacionRepository).save(any(Cotizacion.class));
    }

    @Test
    void debeAsignarEstadoPendienteAlCrearCotizacion() {

        Proyecto proyecto = crearProyecto();

        CotizacionRequest request = new CotizacionRequest(
                1L,
                new BigDecimal("75000000"),
                LocalDate.of(2026, 10, 30),
                "Propuesta inicial"
        );

        when(proyectoRepository.findById(1L))
                .thenReturn(Optional.of(proyecto));

        when(cotizacionRepository
                .findTopByProyectoIdOrderByNumeroVersionDesc(any()))
                .thenReturn(Optional.empty());

        when(cotizacionRepository.save(any(Cotizacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = cotizacionService.crearCotizacion(request);

        assertEquals(EstadoCotizacion.PENDIENTE, respuesta.estado());
    }

    @Test
    void debeListarCotizacionesPorProyectoEnOrdenDescendente() {

        Proyecto proyecto = crearProyecto();

        Cotizacion versionUno = crearCotizacion(
                proyecto,
                1,
                EstadoCotizacion.RECHAZADA
        );

        Cotizacion versionDos = crearCotizacion(
                proyecto,
                2,
                EstadoCotizacion.PENDIENTE
        );

        when(proyectoRepository.existsById(1L))
                .thenReturn(true);

        when(cotizacionRepository
                .findByProyectoIdOrderByNumeroVersionDesc(1L))
                .thenReturn(List.of(versionDos, versionUno));

        var respuestas = cotizacionService.listarCotizacionesPorProyecto(1L);

        assertEquals(2, respuestas.size());
        assertEquals(2, respuestas.get(0).numeroVersion());
        assertEquals(1, respuestas.get(1).numeroVersion());
    }

    @Test
    void debeBuscarCotizacionPorId() {

        Proyecto proyecto = crearProyecto();

        Cotizacion cotizacion = crearCotizacion(
                proyecto,
                1,
                EstadoCotizacion.PENDIENTE
        );

        when(cotizacionRepository.findById(1L))
                .thenReturn(Optional.of(cotizacion));

        var respuesta = cotizacionService.buscarCotizacionPorId(1L);

        assertEquals(1, respuesta.numeroVersion());
        assertEquals("Diseño de vivienda", respuesta.nombreProyecto());
        assertEquals(EstadoCotizacion.PENDIENTE, respuesta.estado());
    }

    @Test
    void debeActualizarEstadoSinModificarOtrosDatos() {

        Proyecto proyecto = crearProyecto();

        Cotizacion cotizacion = crearCotizacion(
                proyecto,
                1,
                EstadoCotizacion.PENDIENTE
        );

        when(cotizacionRepository.findById(1L))
                .thenReturn(Optional.of(cotizacion));

        when(proyectoRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(proyecto));

        when(cotizacionRepository
                .findByProyectoIdAndEstadoAndIdNot(
                        1L,
                        EstadoCotizacion.ACEPTADA,
                        1L
                ))
                .thenReturn(List.of());

        when(cotizacionRepository.save(any(Cotizacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = cotizacionService.actualizarEstado(
                1L,
                EstadoCotizacion.ACEPTADA
        );

        assertEquals(EstadoCotizacion.ACEPTADA, respuesta.estado());
        assertEquals(1, respuesta.numeroVersion());
        assertEquals("50000000", respuesta.valor().toPlainString());
        assertEquals("Diseño arquitectónico", respuesta.descripcion());

        verify(cotizacionRepository).save(cotizacion);
    }

    @Test
    void debeDevolverCotizacionAceptadaAnteriorAPendiente() {

        Proyecto proyecto = crearProyecto();

        Cotizacion anterior = crearCotizacion(
                proyecto,
                1,
                EstadoCotizacion.ACEPTADA
        );

        Cotizacion nueva = crearCotizacion(
                proyecto,
                2,
                EstadoCotizacion.PENDIENTE
        );

        when(cotizacionRepository.findById(2L))
                .thenReturn(Optional.of(nueva));

        when(proyectoRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(proyecto));

        when(cotizacionRepository
                .findByProyectoIdAndEstadoAndIdNot(
                        1L,
                        EstadoCotizacion.ACEPTADA,
                        2L
                ))
                .thenReturn(List.of(anterior));

        when(cotizacionRepository.save(any(Cotizacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = cotizacionService.actualizarEstado(
                2L,
                EstadoCotizacion.ACEPTADA
        );

        assertEquals(EstadoCotizacion.PENDIENTE, anterior.getEstado());
        assertEquals(EstadoCotizacion.ACEPTADA, nueva.getEstado());
        assertEquals(EstadoCotizacion.ACEPTADA, respuesta.estado());

        verify(cotizacionRepository).saveAll(List.of(anterior));
        verify(cotizacionRepository).save(nueva);
    }

    @Test
    void noDebeModificarCotizacionesAceptadasDeOtrosProyectos() {

        Proyecto proyectoUno = crearProyecto();

        Proyecto proyectoDos = new Proyecto();
        asignarId(proyectoDos, 2L);
        proyectoDos.setNombre("Remodelación de oficina");

        Cotizacion anterior = crearCotizacion(
                proyectoUno,
                1,
                EstadoCotizacion.ACEPTADA
        );

        Cotizacion deOtroProyecto = crearCotizacion(
                proyectoDos,
                2,
                EstadoCotizacion.ACEPTADA
        );

        Cotizacion nueva = crearCotizacion(
                proyectoUno,
                3,
                EstadoCotizacion.PENDIENTE
        );

        when(cotizacionRepository.findById(3L))
                .thenReturn(Optional.of(nueva));

        when(proyectoRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(proyectoUno));

        when(cotizacionRepository
                .findByProyectoIdAndEstadoAndIdNot(
                        1L,
                        EstadoCotizacion.ACEPTADA,
                        3L
                ))
                .thenReturn(List.of(anterior));

        when(cotizacionRepository.save(any(Cotizacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        cotizacionService.actualizarEstado(
                3L,
                EstadoCotizacion.ACEPTADA
        );

        assertEquals(EstadoCotizacion.PENDIENTE, anterior.getEstado());
        assertEquals(EstadoCotizacion.ACEPTADA, deOtroProyecto.getEstado());

        verify(cotizacionRepository).saveAll(List.of(anterior));
    }

    @Test
    void noDebeModificarOtrasCotizacionesAlRechazarUna() {

        Proyecto proyecto = crearProyecto();

        Cotizacion cotizacion = crearCotizacion(
                proyecto,
                1,
                EstadoCotizacion.PENDIENTE
        );

        Cotizacion otraAceptada = crearCotizacion(
                proyecto,
                2,
                EstadoCotizacion.ACEPTADA
        );

        when(cotizacionRepository.findById(1L))
                .thenReturn(Optional.of(cotizacion));

        when(cotizacionRepository.save(any(Cotizacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = cotizacionService.actualizarEstado(
                1L,
                EstadoCotizacion.RECHAZADA
        );

        assertEquals(EstadoCotizacion.RECHAZADA, respuesta.estado());
        assertEquals(EstadoCotizacion.ACEPTADA, otraAceptada.getEstado());

        verify(proyectoRepository, never()).findByIdForUpdate(any());
        verify(cotizacionRepository, never())
                .findByProyectoIdAndEstadoAndIdNot(any(), any(), any());
        verify(cotizacionRepository, never()).saveAll(any());
    }

    @Test
    void noDebeCrearCotizacionParaProyectoInexistente() {

        CotizacionRequest request = new CotizacionRequest(
                99L,
                new BigDecimal("50000000"),
                LocalDate.of(2026, 10, 30),
                "Diseño arquitectónico"
        );

        when(proyectoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> cotizacionService.crearCotizacion(request)
        );

        verify(cotizacionRepository, never()).save(any(Cotizacion.class));
    }

    @Test
    void noDebeConsultarNiActualizarCotizacionInexistente() {

        when(cotizacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> cotizacionService.buscarCotizacionPorId(99L)
        );

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> cotizacionService.actualizarEstado(
                        99L,
                        EstadoCotizacion.ACEPTADA
                )
        );

        verify(cotizacionRepository, never()).save(any(Cotizacion.class));
    }
}
