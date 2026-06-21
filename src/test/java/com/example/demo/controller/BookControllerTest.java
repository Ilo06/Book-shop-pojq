package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateBookDTO;
import com.example.demo.dto.response.AuthorResponse;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.dto.response.BookSummaryResponse;
import com.example.demo.dto.response.GenreResponse;
import com.example.demo.endpoint.rest.controller.bookshop.BookController;
import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Genre;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.BookCopyService;
import com.example.demo.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest({BookController.class, GlobalExceptionHandler.class})
class BookControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookService bookService;

  @MockBean private BookCopyService bookCopyService;

  private Book book;

  private Author author;
  private AuthorResponse authorResponse;

  private Genre genre;
  private GenreResponse genreResponse;

  @BeforeEach
  void setUp() {
    genre = Genre.builder().id(UUID.randomUUID()).name("Horror").build();
    genreResponse = GenreResponse.builder().id(genre.getId()).name(genre.getName()).build();

    author = Author.builder().id(UUID.randomUUID()).firstName("Jean").lastName("Paul").build();
    authorResponse =
        AuthorResponse.builder()
            .id(author.getId())
            .firstName(author.getFirstName())
            .lastName(author.getLastName())
            .build();

    book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("A random Book")
            .isbn("1234567891234")
            .description("A book description")
            .publishDate(LocalDate.now())
            .genre(genre)
            .authors(List.of(author))
            .build();
  }

  @Test
  void getById_shouldReturn200_on_validBook() throws Exception {
    BookResponse response =
        BookResponse.builder()
            .id(book.getId())
            .title(book.getTitle())
            .isbn(book.getIsbn())
            .description(book.getDescription())
            .publishDate(book.getPublishDate())
            .genre(genreResponse)
            .authors(List.of(authorResponse))
            .build();

    when(bookService.findById(book.getId())).thenReturn(response);

    mockMvc
        .perform(get("/books/{bookId}", book.getId()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(book.getId().toString()))
        .andExpect(jsonPath("$.title").value(book.getTitle()))
        .andExpect(jsonPath("$.isbn").value(book.getIsbn()));
  }

  @Test
  void getById_shouldReturn400_on_badId() throws Exception {
    mockMvc
        .perform(get("/books/a").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getById_shouldReturn404_on_nonexistentBook() throws Exception {
    UUID id = UUID.randomUUID();
    when(bookService.findById(id))
        .thenThrow(new ResourceNotFoundException("Book not found with id: " + id));

    mockMvc
        .perform(get("/books/{bookId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void listBooks_shouldReturn200() throws Exception {
    BookSummaryResponse summary =
        BookSummaryResponse.builder()
            .id(book.getId())
            .title(book.getTitle())
            .isbn(book.getIsbn())
            .genre(genreResponse)
            .build();

    when(bookService.findAll(null, null, null)).thenReturn(List.of(summary));

    mockMvc
        .perform(get("/books").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(book.getId().toString()))
        .andExpect(jsonPath("$[0].title").value(book.getTitle()));
  }

  @Test
  void createBook_shouldReturn201() throws Exception {
    CreateBookDTO input = new CreateBookDTO();
    input.setTitle("New Book");
    input.setIsbn("9789999999999");
    input.setDescription("A new book description");
    input.setPublishDate(LocalDate.of(2026, 6, 1));
    input.setGenreId(genre.getId());
    input.setAuthorIds(List.of(author.getId()));

    BookResponse response =
        BookResponse.builder()
            .id(UUID.randomUUID())
            .title(input.getTitle())
            .isbn(input.getIsbn())
            .description(input.getDescription())
            .publishDate(input.getPublishDate())
            .genre(genreResponse)
            .authors(List.of(authorResponse))
            .build();

    when(bookService.create(any(CreateBookDTO.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value(input.getTitle()))
        .andExpect(jsonPath("$.isbn").value(input.getIsbn()));
  }

  @Test
  void createBook_shouldReturn400_onValidationError() throws Exception {
    CreateBookDTO input = new CreateBookDTO();
    input.setTitle("Sorry, no title");
    input.setIsbn("ohhh");

    mockMvc
        .perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createBook_shouldReturn409_onIsbnConflict() throws Exception {
    CreateBookDTO input = new CreateBookDTO();
    input.setTitle("New Book");
    input.setIsbn("9788888888888");
    input.setPublishDate(LocalDate.of(2026, 6, 1));
    input.setGenreId(genre.getId());
    input.setAuthorIds(List.of(author.getId()));

    when(bookService.create(any(CreateBookDTO.class)))
        .thenThrow(
            new ResourceConflictException(
                "Book with ISBN '" + input.getIsbn() + "' already exists"));

    mockMvc
        .perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isConflict());
  }

  @Test
  void updateBook_shouldReturn200() throws Exception {
    CreateBookDTO input = new CreateBookDTO();
    input.setTitle("Updated Book");
    input.setIsbn("9787777777777");
    input.setDescription("Updated description");
    input.setPublishDate(LocalDate.of(2026, 7, 1));
    input.setGenreId(genre.getId());
    input.setAuthorIds(List.of(author.getId()));

    BookResponse response =
        BookResponse.builder()
            .id(book.getId())
            .title(input.getTitle())
            .isbn(input.getIsbn())
            .description(input.getDescription())
            .publishDate(input.getPublishDate())
            .genre(genreResponse)
            .authors(List.of(authorResponse))
            .build();

    when(bookService.update(eq(book.getId()), any(CreateBookDTO.class))).thenReturn(response);

    mockMvc
        .perform(
            put("/books/{bookId}", book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value(input.getTitle()))
        .andExpect(jsonPath("$.isbn").value(input.getIsbn()));
  }

  @Test
  void updateBook_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    CreateBookDTO input = new CreateBookDTO();
    input.setTitle("Updated Book");
    input.setIsbn("9786666666666");
    input.setPublishDate(LocalDate.of(2026, 7, 1));
    input.setGenreId(genre.getId());
    input.setAuthorIds(List.of(author.getId()));

    when(bookService.update(eq(id), any(CreateBookDTO.class)))
        .thenThrow(new ResourceNotFoundException("Book not found with id: " + id));

    mockMvc
        .perform(
            put("/books/{bookId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteBook_shouldReturn204() throws Exception {
    doNothing().when(bookService).delete(book.getId());

    mockMvc.perform(delete("/books/{bookId}", book.getId())).andExpect(status().isNoContent());

    verify(bookService).delete(book.getId());
  }

  @Test
  void deleteBook_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    doThrow(new ResourceNotFoundException("Book not found with id: " + id))
        .when(bookService)
        .delete(id);

    mockMvc.perform(delete("/books/{bookId}", id)).andExpect(status().isNotFound());
  }
}
