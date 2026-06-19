package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateBookDTO;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.dto.response.BookSummaryResponse;
import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Genre;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.AuthorRepository;
import com.example.demo.repository.bookshop.BookRepository;
import com.example.demo.repository.bookshop.GenreRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock BookRepository bookRepository;
  @Mock GenreRepository genreRepository;
  @Mock AuthorRepository authorRepository;
  BookService service;
  Genre genre;
  Author author;
  Book book;
  UUID id, genreId, authorId;

  @BeforeEach
  void setUp() {
    service = new BookService(bookRepository, genreRepository, authorRepository);
    id = UUID.randomUUID();
    genreId = UUID.randomUUID();
    authorId = UUID.randomUUID();
    genre = Genre.builder().id(genreId).name("Fiction").build();
    author = Author.builder().id(authorId).firstName("John").lastName("Doe").build();
    book =
        Book.builder()
            .id(id)
            .title("Test Book")
            .isbn("9781234567890")
            .publishDate(LocalDate.of(2024, 1, 1))
            .genre(genre)
            .authors(List.of(author))
            .build();
  }

  @Test
  void findAll_withoutFilters_returnsAll() {
    when(bookRepository.findAll()).thenReturn(List.of(book));

    List<BookSummaryResponse> result = service.findAll(null, null, null);

    assertEquals(1, result.size());
    assertEquals("Test Book", result.get(0).getTitle());
  }

  @Test
  void findAll_withFilters_returnsFiltered() {
    when(bookRepository.findByFilters(genreId, authorId, "test")).thenReturn(List.of(book));

    List<BookSummaryResponse> result = service.findAll(genreId, authorId, "test");

    assertEquals(1, result.size());
  }

  @Test
  void findAll_withBlankSearchIgnored() {
    when(bookRepository.findAll()).thenReturn(List.of(book));

    List<BookSummaryResponse> result = service.findAll(null, null, "   ");

    assertEquals(1, result.size());
  }

  @Test
  void findById_returnsBook() {
    when(bookRepository.findById(id)).thenReturn(Optional.of(book));

    BookResponse result = service.findById(id);

    assertEquals("Test Book", result.getTitle());
    assertEquals("Fiction", result.getGenre().getName());
  }

  @Test
  void findById_throwsWhenNotFound() {
    when(bookRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
  }

  @Test
  void create_savesBook() {
    CreateBookDTO input = new CreateBookDTO("New Book", "9789876543210", "Desc", LocalDate.of(2024, 6, 1), genreId, List.of(authorId));
    when(bookRepository.existsByIsbn("9789876543210")).thenReturn(false);
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(bookRepository.save(any())).thenReturn(book);

    BookResponse result = service.create(input);

    assertNotNull(result);
  }

  @Test
  void create_throwsOnDuplicateIsbn() {
    CreateBookDTO input = new CreateBookDTO("New", "9781234567890", null, LocalDate.now(), genreId, List.of(authorId));
    when(bookRepository.existsByIsbn("9781234567890")).thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> service.create(input));
  }

  @Test
  void create_throwsWhenGenreNotFound() {
    CreateBookDTO input = new CreateBookDTO("New", "9789876543210", null, LocalDate.now(), genreId, List.of(authorId));
    when(bookRepository.existsByIsbn("9789876543210")).thenReturn(false);
    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.create(input));
  }

  @Test
  void create_throwsWhenAuthorNotFound() {
    CreateBookDTO input = new CreateBookDTO("New", "9789876543210", null, LocalDate.now(), genreId, List.of(authorId));
    when(bookRepository.existsByIsbn("9789876543210")).thenReturn(false);
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.create(input));
  }

  @Test
  void create_withEmptyAuthors_saves() {
    CreateBookDTO input = new CreateBookDTO("New", "9789876543210", null, LocalDate.now(), genreId, List.of());
    when(bookRepository.existsByIsbn("9789876543210")).thenReturn(false);
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(bookRepository.save(any())).thenReturn(book);

    BookResponse result = service.create(input);

    assertNotNull(result);
  }

  @Test
  void update_changesBook() {
    CreateBookDTO input = new CreateBookDTO("Updated", "9781234567890", null, LocalDate.of(2024, 6, 1), genreId, List.of(authorId));
    when(bookRepository.findById(id)).thenReturn(Optional.of(book));
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(bookRepository.save(any())).thenReturn(book);

    BookResponse result = service.update(id, input);

    assertNotNull(result);
  }

  @Test
  void update_throwsOnDuplicateIsbn() {
    Book other = Book.builder().id(UUID.randomUUID()).isbn("Different").build();
    CreateBookDTO input = new CreateBookDTO("Updated", "9781234567890", null, LocalDate.now(), genreId, List.of(authorId));
    when(bookRepository.findById(id)).thenReturn(Optional.of(other));
    when(bookRepository.existsByIsbn("9781234567890")).thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> service.update(id, input));
  }

  @Test
  void delete_removesBook() {
    when(bookRepository.findById(id)).thenReturn(Optional.of(book));

    service.delete(id);

    verify(bookRepository).delete(book);
  }

  @Test
  void delete_throwsWhenNotFound() {
    when(bookRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
  }

  @Test
  void getOrThrow_returnsBook() {
    when(bookRepository.findById(id)).thenReturn(Optional.of(book));

    Book result = service.getOrThrow(id);

    assertEquals(id, result.getId());
  }

  @Test
  void getOrThrow_throwsWhenNotFound() {
    when(bookRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getOrThrow(id));
  }
}
