package com.example.demo.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class GlobalExceptionHandlerTest {

  GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleResourceNotFound() {
    ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
    ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Not Found", response.getBody().getError());
    assertEquals("Not found", response.getBody().getMessage());
  }

  @Test
  void handleResourceConflict() {
    ResourceConflictException ex = new ResourceConflictException("Conflict");
    ResponseEntity<ErrorResponse> response = handler.handleResourceConflict(ex);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Conflict", response.getBody().getError());
  }

  @Test
  void handleUnprocessableEntity() {
    UnprocessableEntityException ex = new UnprocessableEntityException("Invalid");
    ResponseEntity<ErrorResponse> response = handler.handleUnprocessableEntity(ex);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertEquals("Unprocessable Entity", response.getBody().getError());
  }

  @Test
  void handleValidationException() {
    var ex = mock(org.springframework.web.bind.MethodArgumentNotValidException.class);
    var bindingResult = mock(BindingResult.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of());

    ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody().getMessage().contains("Invalid input data"));
  }

  @Test
  void handleMalformedJson() {
    HttpMessageNotReadableException ex = new HttpMessageNotReadableException("malformed");
    ResponseEntity<ErrorResponse> response = handler.handleMalformedJson(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody().getMessage().contains("Malformed JSON"));
  }

  @Test
  void handleTypeMismatch() {
    MethodArgumentTypeMismatchException ex =
        new MethodArgumentTypeMismatchException("bad", String.class, "param", null, null);
    ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody().getMessage().contains("could not be converted"));
  }

  @Test
  void handleGeneralException() {
    Exception ex = new RuntimeException("Unexpected error");
    ResponseEntity<ErrorResponse> response = handler.handleGeneralException(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal Server Error", response.getBody().getError());
  }

  @Test
  void errorResponse_constructorAndGetters() {
    ErrorResponse error = new ErrorResponse(400, "Bad Request", "msg", LocalDateTime.now());
    assertEquals(400, error.getStatus());
    assertEquals("Bad Request", error.getError());
    assertEquals("msg", error.getMessage());
    assertNotNull(error.getTimestamp());
  }

  @Test
  void errorResponse_noArgsConstructor() {
    ErrorResponse error = new ErrorResponse();
    assertNotNull(error);
  }
}
