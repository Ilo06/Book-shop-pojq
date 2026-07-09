package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateBookCopyDTO;
import com.example.demo.dto.request.PatchBookCopyDTO;
import com.example.demo.dto.response.BookCopyResponse;
import com.example.demo.endpoint.rest.controller.bookshop.BookCopyController;
import com.example.demo.entity.Book;
import com.example.demo.entity.Genre;
import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.BookCopyService;
import com.example.demo.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({BookCopyController.class, GlobalExceptionHandler.class})
class BookCopyControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private BookService bookService;
  @MockBean private BookCopyService bookCopyService;

  private UUID bookId;
  private UUID copyId;
  private BookCopyResponse bookCopyResponse;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();
    copyId = UUID.randomUUID();
    bookCopyResponse =
        BookCopyResponse.builder()
            .id(copyId)
            .bookId(bookId)
            .type(BookCopyType.PAPERBACK)
            .price(BigDecimal.valueOf(19.99))
            .location("Shelf A1")
            .build();
  }

  @Test
  void listBookCopies_shouldReturn200() throws Exception {
    when(bookService.getOrThrow(bookId))
        .thenReturn(
            Book.builder()
                .id(bookId)
                .title("Test")
                .isbn("9781234567890")
                .publishDate(LocalDate.now())
                .genres(List.of(Genre.builder().id(UUID.randomUUID()).name("Fiction").build()))
                .build());
    when(bookCopyService.findByBookId(bookId)).thenReturn(List.of(bookCopyResponse));

    mockMvc
        .perform(get("/books/{bookId}/copies", bookId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(copyId.toString()));
  }

  @Test
  void getBookCopy_shouldReturn200() throws Exception {
    when(bookCopyService.findById(copyId)).thenReturn(bookCopyResponse);

    mockMvc
        .perform(
            get("/books/{bookId}/copies/{copyId}", bookId, copyId)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(copyId.toString()));
  }

  @Test
  void getBookCopy_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    when(bookCopyService.findById(id))
        .thenThrow(new ResourceNotFoundException("BookCopy not found with id: " + id));

    mockMvc
        .perform(
            get("/books/{bookId}/copies/{copyId}", bookId, id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void createBookCopy_shouldReturn201() throws Exception {
    CreateBookCopyDTO input = new CreateBookCopyDTO();
    input.setType(BookCopyType.HARDBACK);
    input.setPrice(BigDecimal.valueOf(29.99));
    input.setLocation("Shelf B2");

    when(bookCopyService.create(eq(bookId), any(CreateBookCopyDTO.class)))
        .thenReturn(bookCopyResponse);

    mockMvc
        .perform(
            post("/books/{bookId}/copies", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(copyId.toString()));
  }

  @Test
  void createBookCopy_shouldReturn400_onValidationError() throws Exception {
    CreateBookCopyDTO input = new CreateBookCopyDTO();

    mockMvc
        .perform(
            post("/books/{bookId}/copies", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void patchBookCopy_shouldReturn200() throws Exception {
    PatchBookCopyDTO input = new PatchBookCopyDTO();
    input.setLocation("Shelf C3");

    when(bookCopyService.patch(eq(copyId), any(PatchBookCopyDTO.class)))
        .thenReturn(bookCopyResponse);

    mockMvc
        .perform(
            patch("/books/{bookId}/copies/{copyId}", bookId, copyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(copyId.toString()));
  }

  @Test
  void patchBookCopy_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    PatchBookCopyDTO input = new PatchBookCopyDTO();
    input.setLocation("Shelf Z9");

    when(bookCopyService.patch(eq(id), any(PatchBookCopyDTO.class)))
        .thenThrow(new ResourceNotFoundException("BookCopy not found with id: " + id));

    mockMvc
        .perform(
            patch("/books/{bookId}/copies/{copyId}", bookId, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteBookCopy_shouldReturn204() throws Exception {
    doNothing().when(bookCopyService).delete(copyId);

    mockMvc
        .perform(delete("/books/{bookId}/copies/{copyId}", bookId, copyId))
        .andExpect(status().isCreated());

    verify(bookCopyService).delete(copyId);
  }

  @Test
  void deleteBookCopy_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    doThrow(new ResourceNotFoundException("BookCopy not found with id: " + id))
        .when(bookCopyService)
        .delete(id);

    mockMvc
        .perform(delete("/books/{bookId}/copies/{copyId}", bookId, id))
        .andExpect(status().isNotFound());
  }

  @Test
  void getBookCopyStock_shouldReturn200() throws Exception {
    when(bookCopyService.getStockByCopyId(copyId)).thenReturn(5);

    mockMvc
        .perform(
            get("/books/{bookId}/copies/{copyId}/stock", bookId, copyId)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string("5"));
  }
}
