package com.example.demo.endpoint.rest.controller.bookshop;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.conf.FacadeIT;
import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.entity.enums.BookStatus;
import com.example.demo.entity.enums.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ReservationControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate rest;

  private UUID bookCopyId;

  @BeforeEach
  void setUp() {
    var suffix = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    var genre =
        rest.postForEntity(
                "/api/v1/genres", new CreateGenreDTO("ResGenre" + suffix), GenreResponse.class)
            .getBody();
    var author =
        rest.postForEntity(
                "/api/v1/authors",
                new CreateAuthorDTO("Res" + suffix, "Author"),
                AuthorResponse.class)
            .getBody();
    var bookInput =
        new CreateBookDTO(
            "Reservation Book " + suffix,
            String.format("777%010d", System.nanoTime() % 10000000000L),
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
  void createAndGetReservation() {
    var input =
        new CreateReservationDTO(
            LocalDate.now(), List.of(new QuantifiedBookCopyDTO(bookCopyId, 3)));

    ResponseEntity<ReservationResponse> created =
        rest.postForEntity("/api/v1/reservations", input, ReservationResponse.class);

    assertEquals(HttpStatus.CREATED, created.getStatusCode());
    assertNotNull(created.getBody().getId());
    assertEquals(ReservationStatus.PENDING, created.getBody().getStatus());
    assertEquals(1, created.getBody().getBooks().size());

    var id = created.getBody().getId();

    ResponseEntity<ReservationResponse> fetched =
        rest.getForEntity("/api/v1/reservations/" + id, ReservationResponse.class);

    assertEquals(HttpStatus.OK, fetched.getStatusCode());
    assertEquals(1, fetched.getBody().getBooks().size());
  }

  @Test
  void updateReservationStatus() {
    var input =
        new CreateReservationDTO(
            LocalDate.now(), List.of(new QuantifiedBookCopyDTO(bookCopyId, 1)));
    var created =
        rest.postForEntity("/api/v1/reservations", input, ReservationResponse.class).getBody();

    ResponseEntity<ReservationResponse> updated =
        rest.exchange(
            "/api/v1/reservations/" + created.getId() + "/status?status=CONFIRMED",
            HttpMethod.PATCH,
            null,
            ReservationResponse.class);

    assertEquals(HttpStatus.OK, updated.getStatusCode());
    assertEquals(ReservationStatus.CONFIRMED, updated.getBody().getStatus());
  }

  @Test
  void deleteReservation() {
    var input =
        new CreateReservationDTO(
            LocalDate.now(), List.of(new QuantifiedBookCopyDTO(bookCopyId, 2)));
    var created =
        rest.postForEntity("/api/v1/reservations", input, ReservationResponse.class).getBody();

    rest.delete("/api/v1/reservations/" + created.getId());

    ResponseEntity<String> fetched =
        rest.exchange(
            "/api/v1/reservations/" + created.getId(), HttpMethod.GET, null, String.class);
    assertEquals(HttpStatus.NOT_FOUND, fetched.getStatusCode());
  }
}
