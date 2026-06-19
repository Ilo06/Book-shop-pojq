package com.example.demo.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateAuthorDTO;
import com.example.demo.dto.response.AuthorResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean AuthorService authorService;

  @Test
  void listAuthors_returns200() throws Exception {
    when(authorService.findAll(any(Pageable.class)))
        .thenReturn(new PageResponse<>());
    mockMvc.perform(get("/authors")).andExpect(status().isOk());
  }

  @Test
  void getAuthor_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(authorService.findById(id)).thenReturn(new AuthorResponse());
    mockMvc.perform(get("/authors/{id}", id)).andExpect(status().isOk());
  }

  @Test
  void createAuthor_returns201() throws Exception {
    when(authorService.create(any())).thenReturn(new AuthorResponse());
    mockMvc
        .perform(
            post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateAuthorDTO("John", "Doe"))))
        .andExpect(status().isCreated());
  }

  @Test
  void updateAuthor_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(authorService.update(any(), any())).thenReturn(new AuthorResponse());
    mockMvc
        .perform(
            put("/authors/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateAuthorDTO("Jane", "Smith"))))
        .andExpect(status().isOk());
  }

  @Test
  void deleteAuthor_returns204() throws Exception {
    UUID id = UUID.randomUUID();
    doNothing().when(authorService).delete(id);
    mockMvc.perform(delete("/authors/{id}", id)).andExpect(status().isNoContent());
  }
}
