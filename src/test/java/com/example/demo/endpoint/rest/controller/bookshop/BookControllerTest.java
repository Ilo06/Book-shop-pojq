package com.example.demo.endpoint.rest.controller.bookshop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateBookDTO;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.service.BookCopyService;
import com.example.demo.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
class BookControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean BookService bookService;
  @MockBean BookCopyService bookCopyService;

  @Test
  void listBooks_returns200() throws Exception {
    when(bookService.findAll(null, null, null)).thenReturn(List.of());
    mockMvc.perform(get("/books")).andExpect(status().isOk());
  }

  @Test
  void listBooks_withFilters() throws Exception {
    UUID genreId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    when(bookService.findAll(genreId, authorId, "test")).thenReturn(List.of());
    mockMvc
        .perform(
            get("/books")
                .param("genreId", genreId.toString())
                .param("authorId", authorId.toString())
                .param("search", "test"))
        .andExpect(status().isOk());
  }

  @Test
  void getBook_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(bookService.findById(id)).thenReturn(new BookResponse());
    mockMvc.perform(get("/books/{id}", id)).andExpect(status().isOk());
  }

  @Test
  void createBook_returns201() throws Exception {
    when(bookService.create(any())).thenReturn(new BookResponse());
    CreateBookDTO input =
        new CreateBookDTO(
            "Title",
            "9781234567890",
            "desc",
            LocalDate.now(),
            UUID.randomUUID(),
            List.of(UUID.randomUUID()));
    mockMvc
        .perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isCreated());
  }

  @Test
  void updateBook_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(bookService.update(any(), any())).thenReturn(new BookResponse());
    CreateBookDTO input =
        new CreateBookDTO(
            "Title",
            "9781234567890",
            null,
            LocalDate.now(),
            UUID.randomUUID(),
            List.of(UUID.randomUUID()));
    mockMvc
        .perform(
            put("/books/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isOk());
  }

  @Test
  void deleteBook_returns204() throws Exception {
    UUID id = UUID.randomUUID();
    doNothing().when(bookService).delete(id);
    mockMvc.perform(delete("/books/{id}", id)).andExpect(status().isNoContent());
  }
}
