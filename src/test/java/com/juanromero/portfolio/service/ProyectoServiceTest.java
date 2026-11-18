
package com.juanromero.portfolio.service;

import java.time.LocalDate;
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

import com.juanromero.portfolio.dto.ProyectoRequest;
import com.juanromero.portfolio.entity.Cliente;
import com.juanromero.portfolio.entity.EstadoProyecto;
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

class ProyectoServiceTest {

    private ProyectoRepository proyectoRepository;
    private ClienteRepository clienteRepository;
    private TipoProyectoRepository tipoProyectoRepository;
    private TipoServicioRepository tipoServicioRepository;
    private CotizacionRepository cotizacionRepository;

    private ProyectoService proyectoService;

    @BeforeEach
    void configurar() {

        proyectoRepository = mock(ProyectoRepository.class);
        clienteRepository = mock(ClienteRepository.class);
        tipoProyectoRepository = mock(TipoProyectoRepository.class);
        tipoServicioRepository = mock(TipoServicioRepository.class);
        cotizacionRepository = mock(CotizacionRepository.class);

        proyectoService = new ProyectoService(
                proyectoRepository,
                clienteRepository,
                tipoProyectoRepository,
                tipoServicioRepository,
                cotizacionRepository
        );
    }

    private ProyectoRequest crearRequest(
            LocalDate previstaInicio,
            LocalDate previstaFinalizacion,
            LocalDate realInicio,
            LocalDate realFinalizacion) {

        return new ProyectoRequest(
                "Diseño de vivienda",
                "Ibagué",
                1L,
                1L,
                1L,
                previstaInicio,
                previstaFinalizacion,
                realInicio,
                realFinalizacion,
                EstadoProyecto.NEGOCIACION
        );
    }

    private void configurarRelacionesValidas() {

        Cliente cliente = new Cliente();
        cliente.setNombre("María López");

        TipoProyecto tipoProyecto = new TipoProyecto();
        tipoProyecto.setNombre("Vivienda");

        TipoServicio tipoServicio = new TipoServicio();
        tipoServicio.setNombre("Diseño arquitectónico");

        when(clienteRepository.findById(1L))
                .thenReturn(Optional.of(cliente));

        when(tipoProyectoRepository.findById(1L))
                .thenReturn(Optional.of(tipoProyecto));

        when(tipoServicioRepository.findById(1L))
                .thenReturn(Optional.of(tipoServicio));
    }

    @Test
    void debeCrearProyectoCorrectamente() {

        configurarRelacionesValidas();

        ProyectoRequest request = crearRequest(
                LocalDate.of(2026, 11, 15),
                LocalDate.of(2027, 3, 15),
                null,
                null
        );

        when(proyectoRepository.save(any(Proyecto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = proyectoService.crearProyecto(request);

        assertEquals("Diseño de vivienda", respuesta.nombre());
        assertEquals("Ibagué", respuesta.ubicacion());
        assertEquals("María López", respuesta.nombreCliente());
        assertEquals("Vivienda", respuesta.nombreTipoProyecto());
        assertEquals("Diseño arquitectónico", respuesta.nombreTipoServicio());
        assertEquals(EstadoProyecto.NEGOCIACION, respuesta.estado());

        verify(proyectoRepository).save(any(Proyecto.class));
    }

    @Test
    void noDebeCrearProyectoConClienteInexistente() {

        ProyectoRequest request = crearRequest(null, null, null, null);

        when(clienteRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> proyectoService.crearProyecto(request)
        );

        verify(proyectoRepository, never()).save(any(Proyecto.class));
        verify(tipoProyectoRepository, never()).findById(anyLong());
    }

    @Test
    void noDebeCrearProyectoConTipoProyectoInexistente() {

        ProyectoRequest request = crearRequest(null, null, null, null);

        Cliente cliente = new Cliente();

        when(clienteRepository.findById(1L))
                .thenReturn(Optional.of(cliente));

        when(tipoProyectoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> proyectoService.crearProyecto(request)
        );

        verify(proyectoRepository, never()).save(any(Proyecto.class));
        verify(tipoServicioRepository, never()).findById(anyLong());
    }

    @Test
    void noDebeCrearProyectoConTipoServicioInexistente() {

        ProyectoRequest request = crearRequest(null, null, null, null);

        Cliente cliente = new Cliente();
        TipoProyecto tipoProyecto = new TipoProyecto();

        when(clienteRepository.findById(1L))
                .thenReturn(Optional.of(cliente));

        when(tipoProyectoRepository.findById(1L))
                .thenReturn(Optional.of(tipoProyecto));

        when(tipoServicioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> proyectoService.crearProyecto(request)
        );

        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    @Test
    void noDebeAceptarFechasPrevistasInconsistentes() {

        ProyectoRequest request = crearRequest(
                LocalDate.of(2027, 3, 15),
                LocalDate.of(2026, 11, 15),
                null,
                null
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> proyectoService.crearProyecto(request)
        );

        verify(clienteRepository, never()).findById(anyLong());
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    @Test
    void noDebeAceptarFechasRealesInconsistentes() {

        ProyectoRequest request = crearRequest(
                null,
                null,
                LocalDate.of(2027, 3, 15),
                LocalDate.of(2026, 11, 15)
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> proyectoService.crearProyecto(request)
        );

        verify(clienteRepository, never()).findById(anyLong());
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    @Test
    void noDebeAceptarFechaRealFinalSinFechaRealInicio() {

        ProyectoRequest request = crearRequest(
                null,
                null,
                null,
                LocalDate.of(2027, 3, 15)
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> proyectoService.crearProyecto(request)
        );

        verify(clienteRepository, never()).findById(anyLong());
        verify(proyectoRepository, never()).save(any(Proyecto.class));
    }

    @Test
    void noDebeEliminarProyectoConCotizacionesAsociadas() {

        Long proyectoId = 1L;
        Proyecto proyecto = new Proyecto();

        when(proyectoRepository.findById(proyectoId))
                .thenReturn(Optional.of(proyecto));

        when(cotizacionRepository.existsByProyectoId(proyectoId))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> proyectoService.eliminarProyecto(proyectoId)
        );

        verify(proyectoRepository, never()).delete(any(Proyecto.class));
    }

    @Test
    void debeEliminarProyectoSinCotizacionesAsociadas() {

        Long proyectoId = 1L;
        Proyecto proyecto = new Proyecto();

        when(proyectoRepository.findById(proyectoId))
                .thenReturn(Optional.of(proyecto));

        when(cotizacionRepository.existsByProyectoId(proyectoId))
                .thenReturn(false);

        assertDoesNotThrow(
                () -> proyectoService.eliminarProyecto(proyectoId)
        );

        verify(proyectoRepository).delete(proyecto);
    }

    @Test
    void noDebeEliminarProyectoInexistente() {

        Long proyectoId = 99L;

        when(proyectoRepository.findById(proyectoId))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> proyectoService.eliminarProyecto(proyectoId)
        );

        verify(cotizacionRepository, never()).existsByProyectoId(anyLong());
        verify(proyectoRepository, never()).delete(any(Proyecto.class));
    }
}
