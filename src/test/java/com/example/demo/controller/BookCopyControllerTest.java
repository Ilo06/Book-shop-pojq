package com.example.demo.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateBookCopyDTO;
import com.example.demo.dto.request.PatchBookCopyDTO;
import com.example.demo.dto.response.BookCopyResponse;
import com.example.demo.endpoint.rest.controller.bookshop.BookCopyController;
import com.example.demo.entity.Book;
import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.entity.enums.BookStatus;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.BookCopyService;
import com.example.demo.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
  private Book book;
  private BookCopyResponse bookCopyResponse;
  private CreateBookCopyDTO createInput;
  private PatchBookCopyDTO patchInput;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();
    copyId = UUID.randomUUID();

    book = Book.builder().id(bookId).title("Test Book").build();

    bookCopyResponse =
        BookCopyResponse.builder()
            .id(copyId)
            .bookId(bookId)
            .status(BookStatus.AVAILABLE)
            .type(BookCopyType.PAPERBACK)
            .price(new BigDecimal("19.99"))
            .location("A1")
            .build();

    createInput = new CreateBookCopyDTO();
    createInput.setBookId(bookId);
    createInput.setType(BookCopyType.PAPERBACK);
    createInput.setPrice(new BigDecimal("19.99"));
    createInput.setLocation("A1");

    patchInput = new PatchBookCopyDTO();
    patchInput.setStatus(BookStatus.SOLD_OUT);
  }

  @Test
  void listBookCopies_shouldReturn200() throws Exception {
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyService.findAll(null)).thenReturn(List.of(bookCopyResponse));

    mockMvc
        .perform(get("/books/{bookId}/copies", bookId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(copyId.toString()))
        .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
  }

  @Test
  void listBookCopies_shouldReturn200_withStatusFilter() throws Exception {
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyService.findAll(BookStatus.AVAILABLE)).thenReturn(List.of(bookCopyResponse));

    mockMvc
        .perform(
            get("/books/{bookId}/copies", bookId)
                .param("status", "AVAILABLE")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void listBookCopies_shouldReturn400_withInvalidBookId() throws Exception {
    mockMvc
        .perform(get("/books/invalid/copies").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void listBookCopies_shouldReturn404_withNonExistingBook() throws Exception {
    when(bookService.getOrThrow(bookId))
        .thenThrow(new ResourceNotFoundException("Book not found with id: " + bookId));

    mockMvc
        .perform(get("/books/{bookId}/copies", bookId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void getBookCopy_shouldReturn200() throws Exception {
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyService.findById(copyId)).thenReturn(bookCopyResponse);

    mockMvc
        .perform(
            get("/books/{bookId}/copies/{copyId}", bookId, copyId)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(copyId.toString()))
        .andExpect(jsonPath("$.status").value("AVAILABLE"))
        .andExpect(jsonPath("$.bookId").value(bookId.toString()));
  }

  @Test
  void getBookCopy_shouldReturn404_withNonExistingCopy() throws Exception {
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    UUID nonExistentCopyId = UUID.randomUUID();
    when(bookCopyService.findById(nonExistentCopyId))
        .thenThrow(
            new ResourceNotFoundException(
                "BookCopy not found with id: " + nonExistentCopyId));

    mockMvc
        .perform(
            get("/books/{bookId}/copies/{copyId}", bookId, nonExistentCopyId)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void getBookCopy_shouldReturn400_withInvalidCopyId() throws Exception {
    mockMvc
        .perform(
            get("/books/{bookId}/copies/invalid", bookId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createBookCopy_shouldReturn201() throws Exception {
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyService.create(any(CreateBookCopyDTO.class))).thenReturn(bookCopyResponse);

    mockMvc
        .perform(
            post("/books/{bookId}/copies", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createInput)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(copyId.toString()))
        .andExpect(jsonPath("$.status").value("AVAILABLE"))
        .andExpect(jsonPath("$.type").value("PAPERBACK"));
  }

  @Test
  void createBookCopy_shouldReturn400_withValidationError() throws Exception {
    CreateBookCopyDTO invalidInput = new CreateBookCopyDTO();

    mockMvc
        .perform(
            post("/books/{bookId}/copies", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidInput)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void patchBookCopy_shouldReturn200() throws Exception {
    BookCopyResponse patchedResponse =
        BookCopyResponse.builder()
            .id(copyId)
            .bookId(bookId)
            .status(BookStatus.SOLD_OUT)
            .type(BookCopyType.PAPERBACK)
            .price(new BigDecimal("19.99"))
            .location("A1")
            .build();

    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyService.patch(eq(copyId), any(PatchBookCopyDTO.class)))
        .thenReturn(patchedResponse);

    mockMvc
        .perform(
            patch("/books/{bookId}/copies/{copyId}", bookId, copyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchInput)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value("SOLD_OUT"));
  }
}
