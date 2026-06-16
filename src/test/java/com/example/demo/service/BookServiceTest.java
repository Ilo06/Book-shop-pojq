package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateBookDTO;
import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Genre;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.repository.bookshop.AuthorRepository;
import com.example.demo.repository.bookshop.BookRepository;
import com.example.demo.repository.bookshop.GenreRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock private BookRepository bookRepository;
  @Mock private GenreRepository genreRepository;
  @Mock private AuthorRepository authorRepository;

  @InjectMocks private BookService bookService;

  @Test
  void findAll_withGenreFilter_callsFindByFilters() {
    var genre = Genre.builder().id(UUID.randomUUID()).name("Fiction").build();
    var book = Book.builder().id(UUID.randomUUID()).title("Test").genre(genre).build();
    when(bookRepository.findByFilters(
            any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(List.of(book));

    var result = bookService.findAll(genre.getId(), null, null);

    assertEquals(1, result.size());
    assertEquals("Test", result.get(0).getTitle());
    verify(bookRepository).findByFilters(genre.getId(), null, null, true, false, false);
  }

  @Test
  void findAll_withSearch_callsFindByFilters() {
    when(bookRepository.findByFilters(
            any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(List.of());

    var result = bookService.findAll(null, null, "harry");

    assertEquals(0, result.size());
    verify(bookRepository).findByFilters(null, null, "%harry%", false, false, true);
  }

  @Test
  void findById_returnsBook() {
    var id = UUID.randomUUID();
    var genre = Genre.builder().id(UUID.randomUUID()).name("Fiction").build();
    var book = Book.builder().id(id).title("Test").genre(genre).build();
    when(bookRepository.findById(id)).thenReturn(Optional.of(book));

    var result = bookService.findById(id);

    assertEquals("Test", result.getTitle());
  }

  @Test
  void create_savesBook() {
    var genreId = UUID.randomUUID();
    var authorId = UUID.randomUUID();
    var genre = Genre.builder().id(genreId).name("Fiction").build();
    var author = Author.builder().id(authorId).firstName("John").lastName("Doe").build();
    var input =
        new CreateBookDTO(
            "Title", "1234567890123", "Desc", LocalDate.now(), genreId, List.of(authorId));

    when(bookRepository.existsByIsbn("1234567890123")).thenReturn(false);
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var result = bookService.create(input);

    assertEquals("Title", result.getTitle());
    assertEquals("1234567890123", result.getIsbn());
  }

  @Test
  void create_throwsWhenIsbnDuplicate() {
    var input =
        new CreateBookDTO(
            "Title", "dup-isbn", "Desc", LocalDate.now(), UUID.randomUUID(), List.of());
    when(bookRepository.existsByIsbn("dup-isbn")).thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> bookService.create(input));
  }

  @Test
  void delete_removesBook() {
    var id = UUID.randomUUID();
    var book = Book.builder().id(id).build();
    when(bookRepository.findById(id)).thenReturn(Optional.of(book));

    bookService.delete(id);

    verify(bookRepository).delete(book);
  }
}
