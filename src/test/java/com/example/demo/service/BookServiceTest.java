package com.example.demo.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    private UUID genreId;
    private UUID authorId;
    private Genre genre;
    private Author author;
    private Book book;
    private CreateBookDTO createBookDTO;

    @BeforeEach
    void setUp() {
        genreId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        genre = Genre.builder()
                .id(genreId)
                .name("Fiction")
                .build();

        author = Author.builder()
                .id(authorId)
                .firstName("John")
                .lastName("Doe")
                .build();

        book = Book.builder()
                .id(bookId)
                .title("Test Book")
                .isbn("9781234567890")
                .description("Test book")
                .publishDate(LocalDate.of(2026, 1, 1))
                .genre(genre)
                .authors(List.of(author))
                .build();

        createBookDTO = new CreateBookDTO();
        createBookDTO.setTitle("New Book");
        createBookDTO.setIsbn("9780987654321");
        createBookDTO.setDescription("New book");
        createBookDTO.setPublishDate(LocalDate.of(2026, 6, 1));
        createBookDTO.setGenreId(genreId);
        createBookDTO.setAuthorIds(List.of(authorId));
    }

    @Test
    void findAll_shouldReturnListOfBookSummaryResponse() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookSummaryResponse> result = bookService.findAll(null, null, null);

        assertEquals(1, result.size());
        BookSummaryResponse summary = result.getFirst();
        assertEquals(book.getId(), summary.getId());
        assertEquals(book.getTitle(), summary.getTitle());
        assertEquals(book.getIsbn(), summary.getIsbn());
        assertNotNull(summary.getGenre());
        assertEquals(genreId, summary.getGenre().getId());
        assertEquals("Fiction", summary.getGenre().getName());
    }

    @Test
    void findById_shouldReturnBookResponse() {
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        BookResponse result = bookService.findById(book.getId());

        assertEquals(book.getId(), result.getId());
        assertEquals(book.getTitle(), result.getTitle());
        assertEquals(book.getIsbn(), result.getIsbn());
        assertEquals(book.getDescription(), result.getDescription());
        assertEquals(book.getPublishDate(), result.getPublishDate());
        assertNotNull(result.getGenre());
        assertEquals(genreId, result.getGenre().getId());
        assertNotNull(result.getAuthors());
        assertEquals(1, result.getAuthors().size());
        assertEquals(authorId, result.getAuthors().getFirst().getId());
    }

    @Test
    void findById_shouldThrowResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.findById(id));
    }

    @Test
    void create_shouldReturnBookResponse() {
        when(bookRepository.existsByIsbn(createBookDTO.getIsbn())).thenReturn(false);
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        BookResponse result = bookService.create(createBookDTO);

        assertNotNull(result.getId());
        assertEquals(createBookDTO.getTitle(), result.getTitle());
        assertEquals(createBookDTO.getIsbn(), result.getIsbn());
        assertEquals(createBookDTO.getDescription(), result.getDescription());
        assertEquals(createBookDTO.getPublishDate(), result.getPublishDate());
        assertNotNull(result.getGenre());
        assertEquals(genreId, result.getGenre().getId());
        assertNotNull(result.getAuthors());
        assertEquals(1, result.getAuthors().size());
        assertEquals(authorId, result.getAuthors().getFirst().getId());
    }

    @Test
    void create_shouldThrowResourceConflictExceptionWhenIsbnExists() {
        when(bookRepository.existsByIsbn(createBookDTO.getIsbn())).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> bookService.create(createBookDTO));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowResourceNotFoundExceptionWhenGenreNotFound() {
        when(bookRepository.existsByIsbn(createBookDTO.getIsbn())).thenReturn(false);
        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.create(createBookDTO));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowResourceNotFoundExceptionWhenAuthorNotFound() {
        when(bookRepository.existsByIsbn(createBookDTO.getIsbn())).thenReturn(false);
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.create(createBookDTO));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void update_shouldReturnUpdatedBookResponse() {
        CreateBookDTO updateDTO = new CreateBookDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setIsbn("9781111111111");
        updateDTO.setDescription("Updated description");
        updateDTO.setPublishDate(LocalDate.of(2025, 1, 1));
        updateDTO.setGenreId(genreId);
        updateDTO.setAuthorIds(List.of(authorId));

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.existsByIsbn(updateDTO.getIsbn())).thenReturn(false);
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse result = bookService.update(book.getId(), updateDTO);

        assertEquals(updateDTO.getTitle(), result.getTitle());
        assertEquals(updateDTO.getIsbn(), result.getIsbn());
        assertEquals(updateDTO.getDescription(), result.getDescription());
        assertEquals(updateDTO.getPublishDate(), result.getPublishDate());
    }

    @Test
    void update_shouldThrowResourceConflictExceptionWhenIsbnAlreadyTaken() {
        CreateBookDTO updateDTO = new CreateBookDTO();
        updateDTO.setIsbn("9789999999999");
        updateDTO.setTitle("Title");
        updateDTO.setPublishDate(LocalDate.now());
        updateDTO.setGenreId(genreId);
        updateDTO.setAuthorIds(List.of(authorId));

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.existsByIsbn(updateDTO.getIsbn())).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> bookService.update(book.getId(), updateDTO));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void update_shouldNotThrowConflictWhenIsbnUnchanged() {
        CreateBookDTO updateDTO = new CreateBookDTO();
        updateDTO.setIsbn(book.getIsbn());
        updateDTO.setTitle("Updated Title");
        updateDTO.setPublishDate(LocalDate.now());
        updateDTO.setGenreId(genreId);
        updateDTO.setAuthorIds(List.of(authorId));

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse result = bookService.update(book.getId(), updateDTO);

        assertEquals("Updated Title", result.getTitle());
    }

    @Test
    void delete_shouldDeleteBook() {
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        bookService.delete(book.getId());

        verify(bookRepository).delete(book);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.delete(id));
        verify(bookRepository, never()).delete(any());
    }

    @Test
    void getOrThrow_shouldReturnBook() {
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        Book result = bookService.getOrThrow(book.getId());

        assertEquals(book.getId(), result.getId());
        assertEquals(book.getTitle(), result.getTitle());
    }

    @Test
    void getOrThrow_shouldThrowResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getOrThrow(id));
    }
}
