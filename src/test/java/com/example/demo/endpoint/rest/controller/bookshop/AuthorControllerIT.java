package com.example.demo.endpoint.rest.controller.bookshop;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.conf.FacadeIT;
import com.example.demo.dto.request.CreateAuthorDTO;
import com.example.demo.dto.response.AuthorResponse;
import com.example.demo.dto.response.PageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AuthorControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate rest;

  @Test
  void createAndGetAuthor() {
    var input = new CreateAuthorDTO("Test", "Author");

    ResponseEntity<AuthorResponse> created =
        rest.postForEntity("/api/v1/authors", input, AuthorResponse.class);

    assertEquals(HttpStatus.CREATED, created.getStatusCode());
    assertNotNull(created.getBody().getId());
    assertEquals("Test", created.getBody().getFirstName());

    var id = created.getBody().getId();

    ResponseEntity<AuthorResponse> fetched =
        rest.getForEntity("/api/v1/authors/" + id, AuthorResponse.class);

    assertEquals(HttpStatus.OK, fetched.getStatusCode());
    assertEquals("Test", fetched.getBody().getFirstName());
  }

  @Test
  void listAuthors() {
    rest.postForEntity("/api/v1/authors", new CreateAuthorDTO("A", "B"), AuthorResponse.class);

    ResponseEntity<PageResponse> page =
        rest.getForEntity("/api/v1/authors?size=20", PageResponse.class);

    assertEquals(HttpStatus.OK, page.getStatusCode());
    assertTrue(page.getBody().getTotalElements() > 0);
  }

  @Test
  void deleteAuthor() {
    var created = rest.postForEntity("/api/v1/authors",
        new CreateAuthorDTO("Delete", "Me"), AuthorResponse.class);
    var id = created.getBody().getId();

    ResponseEntity<Void> deleted =
        rest.exchange("/api/v1/authors/" + id, HttpMethod.DELETE, null, Void.class);

    assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());

    ResponseEntity<AuthorResponse> fetched =
        rest.getForEntity("/api/v1/authors/" + id, AuthorResponse.class);
    assertEquals(HttpStatus.NOT_FOUND, fetched.getStatusCode());
  }
}
