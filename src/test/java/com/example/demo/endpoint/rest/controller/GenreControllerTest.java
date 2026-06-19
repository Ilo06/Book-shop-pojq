package com.example.demo.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateGenreDTO;
import com.example.demo.dto.response.GenreResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.GenreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean GenreService genreService;

  @Test
  void listGenres_returns200() throws Exception {
    when(genreService.findAll(any(Pageable.class))).thenReturn(new PageResponse<>());
    mockMvc.perform(get("/genres")).andExpect(status().isOk());
  }

  @Test
  void getGenre_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(genreService.findById(id)).thenReturn(new GenreResponse());
    mockMvc.perform(get("/genres/{id}", id)).andExpect(status().isOk());
  }

  @Test
  void createGenre_returns201() throws Exception {
    when(genreService.create(any())).thenReturn(new GenreResponse());
    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateGenreDTO("Fiction"))))
        .andExpect(status().isCreated());
  }

  @Test
  void updateGenre_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(genreService.update(any(), any())).thenReturn(new GenreResponse());
    mockMvc
        .perform(
            put("/genres/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateGenreDTO("Sci-Fi"))))
        .andExpect(status().isOk());
  }

  @Test
  void deleteGenre_returns204() throws Exception {
    UUID id = UUID.randomUUID();
    doNothing().when(genreService).delete(id);
    mockMvc.perform(delete("/genres/{id}", id)).andExpect(status().isNoContent());
  }
}
