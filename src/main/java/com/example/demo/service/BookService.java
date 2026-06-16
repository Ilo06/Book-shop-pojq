package com.example.demo.service;

import com.example.demo.dto.request.CreateBookDTO;
import com.example.demo.dto.response.AuthorResponse;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.dto.response.BookSummaryResponse;
import com.example.demo.dto.response.GenreResponse;
import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Genre;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.AuthorRepository;
import com.example.demo.repository.bookshop.BookRepository;
import com.example.demo.repository.bookshop.GenreRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final GenreRepository genreRepository;
  private final AuthorRepository authorRepository;

  @PersistenceContext private EntityManager em;

  @Transactional(readOnly = true)
  public List<BookSummaryResponse> findAll(UUID genreId, UUID authorId, String search) {
    var parts = new ArrayList<String>();
    parts.add("SELECT b FROM Book b WHERE 1=1");
    if (genreId != null) {
      parts.add("AND b.genre.id = :genreId");
    }
    if (authorId != null) {
      parts.add("AND EXISTS (SELECT 1 FROM b.authors a WHERE a.id = :authorId)");
    }
    if (search != null && !search.isBlank()) {
      parts.add(
          "AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.isbn) LIKE"
              + " LOWER(CONCAT('%', :search, '%')))");
    }
    TypedQuery<Book> query = em.createQuery(String.join(" ", parts), Book.class);
    if (genreId != null) {
      query.setParameter("genreId", genreId);
    }
    if (authorId != null) {
      query.setParameter("authorId", authorId);
    }
    if (search != null && !search.isBlank()) {
      query.setParameter("search", search);
    }
    return query.getResultList().stream().map(this::toSummaryResponse).toList();
  }

  public BookResponse findById(UUID id) {
    return toResponse(getOrThrow(id));
  }

  @Transactional
  public BookResponse create(CreateBookDTO input) {
    if (bookRepository.existsByIsbn(input.getIsbn())) {
      throw new ResourceConflictException(
          "Book with ISBN '" + input.getIsbn() + "' already exists");
    }
    Genre genre = getGenreOrThrow(input.getGenreId());
    List<Author> authors = resolveAuthors(input.getAuthorIds());

    Book book =
        Book.builder()
            .title(input.getTitle())
            .isbn(input.getIsbn())
            .description(input.getDescription())
            .publishDate(input.getPublishDate())
            .genre(genre)
            .authors(authors)
            .build();

    return toResponse(bookRepository.save(book));
  }

  @Transactional
  public BookResponse update(UUID id, CreateBookDTO input) {
    Book book = getOrThrow(id);

    if (!book.getIsbn().equals(input.getIsbn()) && bookRepository.existsByIsbn(input.getIsbn())) {
      throw new ResourceConflictException(
          "Book with ISBN '" + input.getIsbn() + "' already exists");
    }

    Genre genre = getGenreOrThrow(input.getGenreId());
    List<Author> authors = resolveAuthors(input.getAuthorIds());

    book.setTitle(input.getTitle());
    book.setIsbn(input.getIsbn());
    book.setDescription(input.getDescription());
    book.setPublishDate(input.getPublishDate());
    book.setGenre(genre);
    book.setAuthors(authors);

    return toResponse(bookRepository.save(book));
  }

  @Transactional
  public void delete(UUID id) {
    Book book = getOrThrow(id);
    bookRepository.delete(book);
  }

  public Book getOrThrow(UUID id) {
    return bookRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
  }

  private Genre getGenreOrThrow(UUID genreId) {
    return genreRepository
        .findById(genreId)
        .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId));
  }

  private List<Author> resolveAuthors(List<UUID> authorIds) {
    if (authorIds == null || authorIds.isEmpty()) {
      return new ArrayList<>();
    }
    return authorIds.stream()
        .map(
            aId ->
                authorRepository
                    .findById(aId)
                    .orElseThrow(
                        () -> new ResourceNotFoundException("Author not found with id: " + aId)))
        .toList();
  }

  private BookSummaryResponse toSummaryResponse(Book book) {
    return BookSummaryResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .isbn(book.getIsbn())
        .genre(
            book.getGenre() != null
                ? GenreResponse.builder()
                    .id(book.getGenre().getId())
                    .name(book.getGenre().getName())
                    .build()
                : null)
        .build();
  }

  private BookResponse toResponse(Book book) {
    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .isbn(book.getIsbn())
        .description(book.getDescription())
        .publishDate(book.getPublishDate())
        .genre(
            book.getGenre() != null
                ? GenreResponse.builder()
                    .id(book.getGenre().getId())
                    .name(book.getGenre().getName())
                    .build()
                : null)
        .authors(
            book.getAuthors() != null
                ? book.getAuthors().stream()
                    .map(
                        a ->
                            AuthorResponse.builder()
                                .id(a.getId())
                                .firstName(a.getFirstName())
                                .lastName(a.getLastName())
                                .build())
                    .toList()
                : List.of())
        .build();
  }
}
