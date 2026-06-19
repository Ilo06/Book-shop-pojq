package com.example.demo.endpoint.rest.controller.bookshop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateBookCopyDTO;
import com.example.demo.dto.request.PatchBookCopyDTO;
import com.example.demo.dto.response.BookCopyResponse;
import com.example.demo.entity.Book;
import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.service.BookCopyService;
import com.example.demo.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookCopyController.class)
class BookCopyControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean BookService bookService;
  @MockBean BookCopyService bookCopyService;

  @Test
  void listBookCopies_returns200() throws Exception {
    UUID bookId = UUID.randomUUID();
    when(bookService.getOrThrow(bookId)).thenReturn(new Book());
    when(bookCopyService.findAll(null)).thenReturn(List.of());
    mockMvc.perform(get("/books/{bookId}/copies", bookId)).andExpect(status().isOk());
  }

  @Test
  void listBookCopies_withStatus() throws Exception {
    UUID bookId = UUID.randomUUID();
    when(bookService.getOrThrow(bookId)).thenReturn(new Book());
    when(bookCopyService.findAll(any())).thenReturn(List.of());
    mockMvc
        .perform(get("/books/{bookId}/copies", bookId).param("status", "AVAILABLE"))
        .andExpect(status().isOk());
  }

  @Test
  void getBookCopy_returns200() throws Exception {
    UUID bookId = UUID.randomUUID();
    UUID copyId = UUID.randomUUID();
    when(bookService.getOrThrow(bookId)).thenReturn(new Book());
    when(bookCopyService.findById(copyId)).thenReturn(new BookCopyResponse());
    mockMvc
        .perform(get("/books/{bookId}/copies/{copyId}", bookId, copyId))
        .andExpect(status().isOk());
  }

  @Test
  void createBookCopy_returns201() throws Exception {
    UUID bookId = UUID.randomUUID();
    when(bookService.getOrThrow(bookId)).thenReturn(new Book());
    when(bookCopyService.create(any())).thenReturn(new BookCopyResponse());
    CreateBookCopyDTO input =
        new CreateBookCopyDTO(bookId, BookCopyType.PAPERBACK, BigDecimal.TEN, "A1", null);
    mockMvc
        .perform(
            post("/books/{bookId}/copies", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isCreated());
  }

  @Test
  void patchBookCopy_returns200() throws Exception {
    UUID bookId = UUID.randomUUID();
    UUID copyId = UUID.randomUUID();
    when(bookService.getOrThrow(bookId)).thenReturn(new Book());
    when(bookCopyService.patch(any(), any())).thenReturn(new BookCopyResponse());
    mockMvc
        .perform(
            patch("/books/{bookId}/copies/{copyId}", bookId, copyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PatchBookCopyDTO())))
        .andExpect(status().isOk());
  }

  @Test
  void deleteBookCopy_returns201() throws Exception {
    UUID bookId = UUID.randomUUID();
    UUID copyId = UUID.randomUUID();
    when(bookService.getOrThrow(bookId)).thenReturn(new Book());
    when(bookCopyService.findById(copyId)).thenReturn(new BookCopyResponse());
    mockMvc
        .perform(delete("/books/{bookId}/copies/{copyId}", bookId, copyId))
        .andExpect(status().isCreated());
  }
}
