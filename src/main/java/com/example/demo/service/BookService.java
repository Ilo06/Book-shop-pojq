package com.example.demo.service;

import com.example.demo.dto.request.CreateBookDTO;
import com.example.demo.dto.response.*;
import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Genre;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.AuthorRepository;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.BookRepository;
import com.example.demo.repository.bookshop.GenreRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final GenreRepository genreRepository;
  private final AuthorRepository authorRepository;
  private final BookCopyRepository bookCopyRepository;

  public List<BookSummaryResponse> findAll(UUID genreId, UUID authorId, String search) {
    List<Book> books =
        (genreId == null && authorId == null && (search == null || search.isBlank()))
            ? bookRepository.findAll()
            : bookRepository.findByFilters(genreId, authorId, search);
    return books.stream().map(this::toSummaryResponse).toList();
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
    List<Genre> genres = new ArrayList<>();
    input.getGenreIds().forEach(id -> genres.add(getGenreOrThrow(id)));
    List<Author> authors = resolveAuthors(input.getAuthorIds());

    Book book =
        Book.builder()
            .title(input.getTitle())
            .isbn(input.getIsbn())
            .description(input.getDescription())
            .publishDate(input.getPublishDate())
            .genres(genres)
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

    List<Genre> genres = new ArrayList<>();
    input.getGenreIds().forEach(genreId -> genres.add(getGenreOrThrow(genreId)));
    List<Author> authors = resolveAuthors(input.getAuthorIds());

    book.setTitle(input.getTitle());
    book.setIsbn(input.getIsbn());
    book.setDescription(input.getDescription());
    book.setPublishDate(input.getPublishDate());
    book.setGenres(genres);
    book.setAuthors(authors);

    return toResponse(bookRepository.save(book));
  }

  @Transactional
  public void delete(UUID id) {
    Book book = getOrThrow(id);
    bookRepository.delete(book);
  }

  public Integer getStock(UUID id) {
    return bookCopyRepository.getBookStock(id);
  }

  public List<CopyStockResponse> getDetailedStock(UUID bookId) {
    return bookCopyRepository.getDetailedBookStock(bookId).stream()
        .map(
            csp ->
                CopyStockResponse.builder()
                    .bookId(bookId)
                    .type(csp.getType())
                    .availableCopies(csp.getAvailableCopies())
                    .build())
        .toList();
  }

  public List<StockResponse> getBooksInStock() {
    return bookCopyRepository.getAllBooksStock().stream()
            .map(
                    p ->
                            StockResponse.builder()
                                    .bookId(p.getBookId())
                                    .title(p.getTitle())
                                    .availableCopies(p.getAvailableCopies())
                                    .build())
            .toList();
  }

  public List<StockResponse> getLowStock() {
    return bookCopyRepository.findLowStockBooks().stream()
            .map(
                    p ->
                            StockResponse.builder()
                                    .bookId(p.getBookId())
                                    .title(p.getTitle())
                                    .availableCopies(p.getAvailableCopies())
                                    .build())
            .toList();
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
        .collect(Collectors.toList());
  }

  private BookSummaryResponse toSummaryResponse(Book book) {
    return BookSummaryResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .isbn(book.getIsbn())
        .genres(
            book.getGenres().stream()
                .map(
                    genre ->
                        GenreResponse.builder().id(genre.getId()).name(genre.getName()).build())
                .toList())
        .build();
  }

  private BookResponse toResponse(Book book) {
    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .isbn(book.getIsbn())
        .description(book.getDescription())
        .publishDate(book.getPublishDate())
        .genres(
            book.getGenres().stream()
                .map(
                    genre ->
                        GenreResponse.builder().id(genre.getId()).name(genre.getName()).build())
                .toList())
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
