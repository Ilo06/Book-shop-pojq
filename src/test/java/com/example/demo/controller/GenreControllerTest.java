package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateGenreDTO;
import com.example.demo.dto.response.GenreResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.endpoint.rest.controller.GenreController;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.GenreService;
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

@WebMvcTest({GenreController.class, GlobalExceptionHandler.class})
class GenreControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private GenreService genreService;

  @Test
  void listGenres_shouldReturn200() throws Exception {
    PageResponse<GenreResponse> pageResponse =
        PageResponse.<GenreResponse>builder().data(List.of()).build();
    when(genreService.findAll(any(Pageable.class))).thenReturn(pageResponse);

    mockMvc
        .perform(get("/genres").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getGenre_shouldReturn200() throws Exception {
    UUID id = UUID.randomUUID();
    GenreResponse response = GenreResponse.builder().id(id).name("Fiction").build();
    when(genreService.findById(id)).thenReturn(response);

    mockMvc
        .perform(get("/genres/{genreId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Fiction"));
  }

  @Test
  void getGenre_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    when(genreService.findById(id))
        .thenThrow(new ResourceNotFoundException("Genre not found with id: " + id));

    mockMvc
        .perform(get("/genres/{genreId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void getGenre_shouldReturn400_onBadId() throws Exception {
    mockMvc
        .perform(get("/genres/a").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createGenre_shouldReturn201() throws Exception {
    CreateGenreDTO input = new CreateGenreDTO("Science-Fiction");
    GenreResponse response =
        GenreResponse.builder().id(UUID.randomUUID()).name("Science-Fiction").build();
    when(genreService.create(any(CreateGenreDTO.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Science-Fiction"));
  }

  @Test
  void createGenre_shouldReturn400_onValidationError() throws Exception {
    CreateGenreDTO input = new CreateGenreDTO("");

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createGenre_shouldReturn409_onNameConflict() throws Exception {
    CreateGenreDTO input = new CreateGenreDTO("Fiction");
    when(genreService.create(any(CreateGenreDTO.class)))
        .thenThrow(new ResourceConflictException("Genre with name 'Fiction' already exists"));

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isConflict());
  }

  @Test
  void updateGenre_shouldReturn200() throws Exception {
    UUID id = UUID.randomUUID();
    CreateGenreDTO input = new CreateGenreDTO("Fantasy");
    GenreResponse response = GenreResponse.builder().id(id).name("Fantasy").build();
    when(genreService.update(eq(id), any(CreateGenreDTO.class))).thenReturn(response);

    mockMvc
        .perform(
            put("/genres/{genreId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Fantasy"));
  }

  @Test
  void updateGenre_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    CreateGenreDTO input = new CreateGenreDTO("Fantasy");
    when(genreService.update(eq(id), any(CreateGenreDTO.class)))
        .thenThrow(new ResourceNotFoundException("Genre not found with id: " + id));

    mockMvc
        .perform(
            put("/genres/{genreId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteGenre_shouldReturn204() throws Exception {
    UUID id = UUID.randomUUID();
    doNothing().when(genreService).delete(id);

    mockMvc.perform(delete("/genres/{genreId}", id)).andExpect(status().isNoContent());

    verify(genreService).delete(id);
  }

  @Test
  void deleteGenre_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    doThrow(new ResourceNotFoundException("Genre not found with id: " + id))
        .when(genreService)
        .delete(id);

    mockMvc.perform(delete("/genres/{genreId}", id)).andExpect(status().isNotFound());
  }

  @Test
  void deleteGenre_shouldReturn409_whenReferenced() throws Exception {
    UUID id = UUID.randomUUID();
    doThrow(
            new ResourceConflictException(
                "Cannot delete genre 'Fiction': it is still referenced by one or more books"))
        .when(genreService)
        .delete(id);

    mockMvc.perform(delete("/genres/{genreId}", id)).andExpect(status().isConflict());
  }
}
