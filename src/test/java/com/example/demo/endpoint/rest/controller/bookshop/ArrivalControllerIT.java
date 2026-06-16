package com.example.demo.endpoint.rest.controller.bookshop;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.conf.FacadeIT;
import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.entity.enums.BookStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ArrivalControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate rest;

  private UUID bookCopyId;

  @BeforeEach
  void setUp() {
    var suffix = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    var genre =
        rest.postForEntity(
                "/api/v1/genres", new CreateGenreDTO("ArrGenre" + suffix), GenreResponse.class)
            .getBody();
    var author =
        rest.postForEntity(
                "/api/v1/authors",
                new CreateAuthorDTO("Arr" + suffix, "Author"),
                AuthorResponse.class)
            .getBody();
    var bookInput =
        new CreateBookDTO(
            "Arrival Book " + suffix,
            String.format("666%010d", System.nanoTime() % 10000000000L),
            "Desc",
            LocalDate.of(2024, 1, 1),
            genre.getId(),
            List.of(author.getId()));
    var bookId =
        rest.postForEntity("/api/v1/books", bookInput, BookResponse.class).getBody().getId();
    var copy =
        rest.postForEntity(
                "/api/v1/book-copies",
                new CreateBookCopyDTO(
                    bookId,
                    BookCopyType.PAPERBACK,
                    BigDecimal.valueOf(10),
                    "Shelf",
                    BookStatus.AVAILABLE),
                BookCopyResponse.class)
            .getBody();
    bookCopyId = copy.getId();
  }

  @Test
  void createAndGetArrival() {
    var input =
        new CreateArrivalDTO(LocalDate.now(), List.of(new QuantifiedBookCopyDTO(bookCopyId, 10)));

    ResponseEntity<ArrivalResponse> created =
        rest.postForEntity("/api/v1/arrivals", input, ArrivalResponse.class);

    assertEquals(HttpStatus.CREATED, created.getStatusCode());
    assertNotNull(created.getBody().getId());
    assertEquals(1, created.getBody().getBooks().size());
    assertEquals(10, created.getBody().getBooks().get(0).getQuantity());

    var id = created.getBody().getId();

    ResponseEntity<ArrivalResponse> fetched =
        rest.getForEntity("/api/v1/arrivals/" + id, ArrivalResponse.class);

    assertEquals(HttpStatus.OK, fetched.getStatusCode());
    assertEquals(1, fetched.getBody().getBooks().size());
  }

  @Test
  void deleteArrival() {
    var input =
        new CreateArrivalDTO(LocalDate.now(), List.of(new QuantifiedBookCopyDTO(bookCopyId, 5)));
    var created = rest.postForEntity("/api/v1/arrivals", input, ArrivalResponse.class).getBody();

    rest.delete("/api/v1/arrivals/" + created.getId());

    ResponseEntity<ArrivalResponse> fetched =
        rest.getForEntity("/api/v1/arrivals/" + created.getId(), ArrivalResponse.class);
    assertEquals(HttpStatus.NOT_FOUND, fetched.getStatusCode());
  }
}
