package com.example.demo.endpoint.rest.controller.bookshop;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.conf.FacadeIT;
import com.example.demo.dto.request.CreateAuthorDTO;
import com.example.demo.dto.request.CreateBookDTO;
import com.example.demo.dto.request.CreateGenreDTO;
import com.example.demo.dto.response.AuthorResponse;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.dto.response.BookSummaryResponse;
import com.example.demo.dto.response.GenreResponse;
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

class BookControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate rest;

  private UUID genreId;
  private UUID authorId;

  @BeforeEach
  void setUp() {
    var suffix = UUID.randomUUID().toString().substring(0, 8);
    var genre =
        rest.postForEntity(
                "/api/v1/genres", new CreateGenreDTO("TestGenre" + suffix), GenreResponse.class)
            .getBody();
    genreId = genre.getId();
    var author =
        rest.postForEntity(
                "/api/v1/authors",
                new CreateAuthorDTO("Test" + suffix, "Author"),
                AuthorResponse.class)
            .getBody();
    authorId = author.getId();
  }

  @Test
  void createAndGetBook() {
    var input =
        new CreateBookDTO(
            "Integration Book",
            "9990000000001",
            "Desc",
            LocalDate.of(2024, 1, 1),
            genreId,
            List.of(authorId));

    ResponseEntity<BookResponse> created =
        rest.postForEntity("/api/v1/books", input, BookResponse.class);

    assertEquals(HttpStatus.CREATED, created.getStatusCode());
    assertNotNull(created.getBody().getId());
    assertEquals("Integration Book", created.getBody().getTitle());
    assertEquals("9990000000001", created.getBody().getIsbn());

    var id = created.getBody().getId();

    ResponseEntity<BookResponse> fetched =
        rest.getForEntity("/api/v1/books/" + id, BookResponse.class);

    assertEquals(HttpStatus.OK, fetched.getStatusCode());
    assertEquals("Integration Book", fetched.getBody().getTitle());
  }

  @Test
  void listBooks_withFilters() {
    var input =
        new CreateBookDTO(
            "Filterable Book",
            "9990000000002",
            "Desc",
            LocalDate.of(2024, 1, 1),
            genreId,
            List.of(authorId));
    rest.postForEntity("/api/v1/books", input, BookResponse.class);

    ResponseEntity<List<BookSummaryResponse>> byGenre =
        rest.exchange(
            "/api/v1/books?genreId=" + genreId,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<BookSummaryResponse>>() {});
    assertEquals(HttpStatus.OK, byGenre.getStatusCode());
    assertTrue(byGenre.getBody().stream().anyMatch(b -> b.getTitle().equals("Filterable Book")));

    ResponseEntity<List<BookSummaryResponse>> bySearch =
        rest.exchange(
            "/api/v1/books?search=Filterable",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<BookSummaryResponse>>() {});
    assertEquals(HttpStatus.OK, bySearch.getStatusCode());
    assertTrue(bySearch.getBody().stream().anyMatch(b -> b.getTitle().equals("Filterable Book")));
  }

  @Test
  void deleteBook() {
    var input =
        new CreateBookDTO(
            "Delete Book",
            "9990000000003",
            "Desc",
            LocalDate.of(2024, 1, 1),
            genreId,
            List.of(authorId));
    var created = rest.postForEntity("/api/v1/books", input, BookResponse.class).getBody();

    rest.delete("/api/v1/books/" + created.getId());

    ResponseEntity<BookResponse> fetched =
        rest.getForEntity("/api/v1/books/" + created.getId(), BookResponse.class);
    assertEquals(HttpStatus.NOT_FOUND, fetched.getStatusCode());
  }
}
