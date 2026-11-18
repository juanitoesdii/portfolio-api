
package com.juanromero.portfolio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juanromero.portfolio.dto.ClienteRequest;
import com.juanromero.portfolio.dto.ClienteResponse;
import com.juanromero.portfolio.entity.Cliente;
import com.juanromero.portfolio.exception.RecursoNoEncontradoException;
import com.juanromero.portfolio.repository.ClienteRepository;
import com.juanromero.portfolio.repository.ProyectoRepository;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ProyectoRepository proyectoRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            ProyectoRepository proyectoRepository) {

        this.clienteRepository = clienteRepository;
        this.proyectoRepository = proyectoRepository;
    }

    public ClienteResponse crearCliente(ClienteRequest request) {

        Cliente cliente = new Cliente();

        cliente.setNombre(request.nombre().trim());
        cliente.setTelefono(request.telefono().trim());
        cliente.setCorreo(normalizarCorreo(request.correo()));
        cliente.setDireccion(normalizarTexto(request.direccion()));

        Cliente clienteGuardado = clienteRepository.save(cliente);

        return convertirAResponse(clienteGuardado);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listarClientes() {

        return clienteRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarClientePorId(Long id) {

        Cliente cliente = obtenerCliente(id);

        return convertirAResponse(cliente);
    }

    public ClienteResponse actualizarCliente(Long id, ClienteRequest request) {

        Cliente cliente = obtenerCliente(id);

        cliente.setNombre(request.nombre().trim());
        cliente.setTelefono(request.telefono().trim());
        cliente.setCorreo(normalizarCorreo(request.correo()));
        cliente.setDireccion(normalizarTexto(request.direccion()));

        Cliente clienteActualizado = clienteRepository.save(cliente);

        return convertirAResponse(clienteActualizado);
    }

    public void eliminarCliente(Long id) {

        Cliente cliente = obtenerCliente(id);

        if (proyectoRepository.existsByClienteId(id)) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el cliente porque tiene proyectos asociados"
            );
        }

        clienteRepository.delete(cliente);
    }

    private Cliente obtenerCliente(Long id) {

        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el cliente con ID: " + id
                ));
    }

    private ClienteResponse convertirAResponse(Cliente cliente) {

        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getCorreo(),
                cliente.getDireccion()
        );
    }

    private String normalizarCorreo(String correo) {

        if (correo == null || correo.isBlank()) {
            return null;
        }

        return correo.trim().toLowerCase();
    }

    private String normalizarTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}
