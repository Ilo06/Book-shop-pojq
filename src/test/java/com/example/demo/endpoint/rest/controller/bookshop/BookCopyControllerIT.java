package com.example.demo.endpoint.rest.controller.bookshop;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.conf.FacadeIT;
import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.entity.enums.BookStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class BookCopyControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate rest;

  private UUID bookId;

  @BeforeEach
  void setUp() {
    var suffix = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    var genre =
        rest.postForEntity(
                "/api/v1/genres", new CreateGenreDTO("ITGenre" + suffix), GenreResponse.class)
            .getBody();
    var author =
        rest.postForEntity(
                "/api/v1/authors",
                new CreateAuthorDTO("IT" + suffix, "Author"),
                AuthorResponse.class)
            .getBody();
    var bookInput =
        new CreateBookDTO(
            "Copy Test Book " + suffix,
            String.format("888%010d", System.nanoTime() % 10000000000L),
            "Desc",
            LocalDate.of(2024, 1, 1),
            genre.getId(),
            List.of(author.getId()));
    bookId = rest.postForEntity("/api/v1/books", bookInput, BookResponse.class).getBody().getId();
  }

  @Test
  void listBookCopies_filtersByBookId() {
    rest.postForEntity(
        "/api/v1/book-copies",
        new CreateBookCopyDTO(bookId, BigDecimal.valueOf(10), "A1", BookStatus.AVAILABLE),
        BookCopyResponse.class);

    ResponseEntity<List<BookCopyResponse>> byBook =
        rest.exchange(
            "/api/v1/book-copies?bookId=" + bookId,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<BookCopyResponse>>() {});
    assertEquals(HttpStatus.OK, byBook.getStatusCode());
    assertEquals(1, byBook.getBody().size());

    ResponseEntity<List<BookCopyResponse>> all =
        rest.exchange(
            "/api/v1/book-copies",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<BookCopyResponse>>() {});
    assertEquals(HttpStatus.OK, all.getStatusCode());
    assertTrue(all.getBody().size() >= 1);
  }

  @Test
  void listBookCopies_filtersByStatus() {
    rest.postForEntity(
        "/api/v1/book-copies",
        new CreateBookCopyDTO(bookId, BigDecimal.valueOf(15), "B1", BookStatus.AVAILABLE),
        BookCopyResponse.class);
    rest.postForEntity(
        "/api/v1/book-copies",
        new CreateBookCopyDTO(bookId, BigDecimal.valueOf(20), "B2", BookStatus.SOLD),
        BookCopyResponse.class);

    ResponseEntity<List<BookCopyResponse>> byStatus =
        rest.exchange(
            "/api/v1/book-copies?status=SOLD",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<BookCopyResponse>>() {});
    assertEquals(1, byStatus.getBody().size());
    assertEquals(BookStatus.SOLD, byStatus.getBody().get(0).getStatus());
  }

  @Test
  void listBookCopies_filtersByBookIdAndStatus() {
    rest.postForEntity(
        "/api/v1/book-copies",
        new CreateBookCopyDTO(bookId, BigDecimal.valueOf(25), "C1", BookStatus.AVAILABLE),
        BookCopyResponse.class);

    ResponseEntity<List<BookCopyResponse>> byBoth =
        rest.exchange(
            "/api/v1/book-copies?bookId=" + bookId + "&status=AVAILABLE",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<BookCopyResponse>>() {});
    assertEquals(HttpStatus.OK, byBoth.getStatusCode());
    assertTrue(byBoth.getBody().stream().allMatch(c -> c.getStatus() == BookStatus.AVAILABLE));
  }
}
