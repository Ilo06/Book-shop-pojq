package com.example.demo.endpoint.rest.controller.bookshop;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.conf.FacadeIT;
import com.example.demo.dto.request.CreateGenreDTO;
import com.example.demo.dto.response.GenreResponse;
import com.example.demo.dto.response.PageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GenreControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate rest;

  @Test
  void createAndGetGenre() {
    var input = new CreateGenreDTO("TestGenre");

    ResponseEntity<GenreResponse> created =
        rest.postForEntity("/api/v1/genres", input, GenreResponse.class);

    assertEquals(HttpStatus.CREATED, created.getStatusCode());
    assertNotNull(created.getBody().getId());
    assertEquals("TestGenre", created.getBody().getName());

    var id = created.getBody().getId();

    ResponseEntity<GenreResponse> fetched =
        rest.getForEntity("/api/v1/genres/" + id, GenreResponse.class);

    assertEquals(HttpStatus.OK, fetched.getStatusCode());
    assertEquals("TestGenre", fetched.getBody().getName());
  }

  @Test
  void listGenres() {
    rest.postForEntity("/api/v1/genres", new CreateGenreDTO("G1"), GenreResponse.class);

    ResponseEntity<PageResponse> page =
        rest.getForEntity("/api/v1/genres?size=20", PageResponse.class);

    assertEquals(HttpStatus.OK, page.getStatusCode());
    assertTrue(page.getBody().getTotalElements() > 0);
  }
}
