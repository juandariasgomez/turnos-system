package com.juand.turnosbackend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Este es un método personalizado para el manejo de Excepciones a nivel general. Por ejemplo: Si se lanza un
   * {@link ResponseStatusException} en la respuesta del servicio saldría así: {"message": "El DNI debe contener 11 dígitos."}
   *
   * @param ex the {@link ResponseStatusException}
   * @return ResponseEntity
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, String>> handle(ResponseStatusException ex) {
    Map<String, String> body = new HashMap<>();
    body.put("message", ex.getReason());
    return ResponseEntity.status(ex.getStatusCode()).body(body);
  }
}
