package com.solarflow.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.*;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now(), "error", "Validation failed", "details", errors, "status", 400));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e) {
        return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now(), "error", e.getMessage(), "status", 400));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        String msg = e.getMessage();
        if (msg != null && msg.toLowerCase().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("timestamp", Instant.now(), "error", msg, "status", 404));
        }
        return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now(), "error", msg, "status", 400));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("timestamp", Instant.now(), "error", e.getMessage(), "status", 404));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("timestamp", Instant.now(), "error", "Data integrity violation: " + e.getMostSpecificCause().getMessage(), "status", 409));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception e) {
        return ResponseEntity.internalServerError().body(Map.of("timestamp", Instant.now(), "error", e.getMessage() == null ? "Internal server error" : e.getMessage(), "status", 500));
    }
}
