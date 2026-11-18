package com.juanromero.portfolio.dto;

public record ClienteResponse(
        Long id,
        String nombre,
        String telefono,
        String correo,
        String direccion
) {
}