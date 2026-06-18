package com.example.demo.exception;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ResourceConflictException.class)
  public ResponseEntity<ErrorResponse> handleResourceConflict(ResourceConflictException ex) {
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(UnprocessableEntityException.class)
  public ResponseEntity<ErrorResponse> handleUnprocessableEntity(UnprocessableEntityException ex) {
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Unprocessable Entity",
            ex.getMessage(),
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    String details =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(java.util.stream.Collectors.joining(", "));
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request (Validation Failed)",
            "Invalid input data. Details: " + details,
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Malformed JSON request body or invalid data type format.",
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

    String targetType =
        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown type";

    String message =
        String.format(
            "The parameter '%s' with value '%s' could not be converted to type '%s'",
            ex.getName(), ex.getValue(), targetType);

    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request (Type Mismatch)",
            message,
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
    log.error(ex.getMessage(), ex);
    ErrorResponse error =
        new ErrorResponse(
            500, "Internal Server Error", "Failed to process request", LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    log.error(ex.getMessage(), ex);
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage(),
            LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
