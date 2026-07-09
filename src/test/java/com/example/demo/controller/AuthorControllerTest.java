package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateAuthorDTO;
import com.example.demo.dto.response.AuthorResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.endpoint.rest.controller.AuthorController;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({AuthorController.class, GlobalExceptionHandler.class})
class AuthorControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private AuthorService authorService;

  @Test
  void listAuthors_shouldReturn200() throws Exception {
    PageResponse<AuthorResponse> pageResponse =
        PageResponse.<AuthorResponse>builder().data(List.of()).build();
    when(authorService.findAll(any(Pageable.class))).thenReturn(pageResponse);

    mockMvc
        .perform(get("/authors").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getAuthor_shouldReturn200() throws Exception {
    UUID id = UUID.randomUUID();
    AuthorResponse response =
        AuthorResponse.builder().id(id).firstName("John").lastName("Doe").build();
    when(authorService.findById(id)).thenReturn(response);

    mockMvc
        .perform(get("/authors/{authorId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"));
  }

  @Test
  void getAuthor_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    when(authorService.findById(id))
        .thenThrow(new ResourceNotFoundException("Author not found with id: " + id));

    mockMvc
        .perform(get("/authors/{authorId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void getAuthor_shouldReturn400_onBadId() throws Exception {
    mockMvc
        .perform(get("/authors/a").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createAuthor_shouldReturn201() throws Exception {
    CreateAuthorDTO input = new CreateAuthorDTO("Jane", "Smith");
    AuthorResponse response =
        AuthorResponse.builder().id(UUID.randomUUID()).firstName("Jane").lastName("Smith").build();
    when(authorService.create(any(CreateAuthorDTO.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value("Jane"))
        .andExpect(jsonPath("$.lastName").value("Smith"));
  }

  @Test
  void createAuthor_shouldReturn400_onValidationError() throws Exception {
    CreateAuthorDTO input = new CreateAuthorDTO("", "");

    mockMvc
        .perform(
            post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createAuthor_shouldReturn409_onNameConflict() throws Exception {
    CreateAuthorDTO input = new CreateAuthorDTO("Existing", "Name");
    when(authorService.create(any(CreateAuthorDTO.class)))
        .thenThrow(
            new ResourceConflictException("Author with name 'Existing Name' already exists"));

    mockMvc
        .perform(
            post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isConflict());
  }

  @Test
  void updateAuthor_shouldReturn200() throws Exception {
    UUID id = UUID.randomUUID();
    CreateAuthorDTO input = new CreateAuthorDTO("Updated", "Name");
    AuthorResponse response =
        AuthorResponse.builder().id(id).firstName("Updated").lastName("Name").build();
    when(authorService.update(eq(id), any(CreateAuthorDTO.class))).thenReturn(response);

    mockMvc
        .perform(
            put("/authors/{authorId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Updated"))
        .andExpect(jsonPath("$.lastName").value("Name"));
  }

  @Test
  void updateAuthor_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    CreateAuthorDTO input = new CreateAuthorDTO("Updated", "Name");
    when(authorService.update(eq(id), any(CreateAuthorDTO.class)))
        .thenThrow(new ResourceNotFoundException("Author not found with id: " + id));

    mockMvc
        .perform(
            put("/authors/{authorId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteAuthor_shouldReturn204() throws Exception {
    UUID id = UUID.randomUUID();
    doNothing().when(authorService).delete(id);

    mockMvc.perform(delete("/authors/{authorId}", id)).andExpect(status().isNoContent());

    verify(authorService).delete(id);
  }

  @Test
  void deleteAuthor_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    doThrow(new ResourceNotFoundException("Author not found with id: " + id))
        .when(authorService)
        .delete(id);

    mockMvc.perform(delete("/authors/{authorId}", id)).andExpect(status().isNotFound());
  }
}
