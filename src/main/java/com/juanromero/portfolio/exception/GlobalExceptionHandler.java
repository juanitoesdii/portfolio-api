package com.juanromero.portfolio.exception;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.juanromero.portfolio.dto.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException exception) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                404,
                "Not Found",
                exception.getMessage(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarErroresValidacion(
            MethodArgumentNotValidException exception) {

        Map<String, String> errores = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (mensajeAnterior, mensajeNuevo) -> mensajeAnterior
                ));

        ApiError error = new ApiError(
                LocalDateTime.now(),
                400,
                "Bad Request",
                "Hay campos con información inválida",
                errores
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ApiError> manejarArgumentoInvalido(
        IllegalArgumentException exception) {

    ApiError error = new ApiError(
            LocalDateTime.now(),
            409,
            "Conflict",
            exception.getMessage(),
            Map.of()
    );

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
}
@ExceptionHandler(ReglaNegocioException.class)
public ResponseEntity<ApiError> manejarReglaNegocio(
        ReglaNegocioException exception) {

    ApiError error = new ApiError(
            LocalDateTime.now(),
            400,
            "Bad Request",
            exception.getMessage(),
            Map.of()
    );

    return ResponseEntity.badRequest().body(error);
}
}