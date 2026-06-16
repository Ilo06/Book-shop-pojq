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

class DashboardControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate rest;

  @BeforeEach
  void setUp() {
    var suffix = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    var genre =
        rest.postForEntity(
                "/api/v1/genres", new CreateGenreDTO("DashGenre" + suffix), GenreResponse.class)
            .getBody();
    var author =
        rest.postForEntity(
                "/api/v1/authors",
                new CreateAuthorDTO("Dash" + suffix, "Author"),
                AuthorResponse.class)
            .getBody();
    var book =
        rest.postForEntity(
                "/api/v1/books",
                new CreateBookDTO(
                    "Dash Book " + suffix,
                    String.format("555%010d", System.nanoTime() % 10000000000L),
                    "Desc",
                    LocalDate.of(2024, 1, 1),
                    genre.getId(),
                    List.of(author.getId())),
                BookResponse.class)
            .getBody();

    rest.postForEntity(
        "/api/v1/book-copies",
        new CreateBookCopyDTO(
            book.getId(),
            BookCopyType.PAPERBACK,
            BigDecimal.valueOf(30),
            "D1",
            BookStatus.AVAILABLE),
        BookCopyResponse.class);
  }

  @Test
  void getTodayRevenue() {
    ResponseEntity<BigDecimal> revenue =
        rest.getForEntity("/api/v1/dashboard/revenue/today", BigDecimal.class);

    assertEquals(HttpStatus.OK, revenue.getStatusCode());
    assertNotNull(revenue.getBody());
  }

  @Test
  void getMonthlyRevenue() {
    ResponseEntity<BigDecimal> revenue =
        rest.getForEntity("/api/v1/dashboard/revenue/monthly", BigDecimal.class);

    assertEquals(HttpStatus.OK, revenue.getStatusCode());
  }

  @Test
  void getTopSellers() {
    ResponseEntity<List> sellers =
        rest.getForEntity("/api/v1/dashboard/top-sellers?limit=5", List.class);

    assertEquals(HttpStatus.OK, sellers.getStatusCode());
  }

  @Test
  void getRevenueByGenre() {
    ResponseEntity<List> byGenre =
        rest.getForEntity("/api/v1/dashboard/revenue/by-genre", List.class);

    assertEquals(HttpStatus.OK, byGenre.getStatusCode());
  }

  @Test
  void getAvailableCopies() {
    ResponseEntity<List> available =
        rest.getForEntity("/api/v1/dashboard/stock/available", List.class);

    assertEquals(HttpStatus.OK, available.getStatusCode());
  }

  @Test
  void getLowStockBooks() {
    ResponseEntity<List> low = rest.getForEntity("/api/v1/dashboard/stock/low", List.class);

    assertEquals(HttpStatus.OK, low.getStatusCode());
  }
}
