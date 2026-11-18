
package com.juanromero.portfolio.service;

import java.util.Optional;

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

import com.juanromero.portfolio.dto.ClienteRequest;
import com.juanromero.portfolio.entity.Cliente;
import com.juanromero.portfolio.exception.RecursoNoEncontradoException;
import com.juanromero.portfolio.repository.ClienteRepository;
import com.juanromero.portfolio.repository.ProyectoRepository;

class ClienteServiceTest {

    private ClienteRepository clienteRepository;
    private ProyectoRepository proyectoRepository;
    private ClienteService clienteService;

    @BeforeEach
    void configurar() {

        clienteRepository = mock(ClienteRepository.class);
        proyectoRepository = mock(ProyectoRepository.class);

        clienteService = new ClienteService(
                clienteRepository,
                proyectoRepository
        );
    }

    @Test
    void debeCrearClienteCorrectamente() {

        ClienteRequest request = new ClienteRequest(
                "María López",
                "3001234567",
                " MARIA@GMAIL.COM ",
                "Ibagué"
        );

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = clienteService.crearCliente(request);

        assertEquals("María López", respuesta.nombre());
        assertEquals("3001234567", respuesta.telefono());
        assertEquals("maria@gmail.com", respuesta.correo());
        assertEquals("Ibagué", respuesta.direccion());

        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void noDebeEliminarClienteConProyectosAsociados() {

        Long clienteId = 1L;

        Cliente cliente = new Cliente();

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(cliente));

        when(proyectoRepository.existsByClienteId(clienteId))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.eliminarCliente(clienteId)
        );

        verify(clienteRepository, never()).delete(any(Cliente.class));
    }

    @Test
    void debeLanzarErrorCuandoClienteNoExiste() {

        Long clienteId = 99L;

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> clienteService.eliminarCliente(clienteId)
        );

        verify(clienteRepository, never()).delete(any(Cliente.class));
        verify(proyectoRepository, never()).existsByClienteId(anyLong());
    }
}
