package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.dto.response.ExternalSearchResponse;
import com.example.demo.exception.BadGatewayException;
import com.example.demo.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;
import java.time.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class ExternalSearchServiceTest {

  private RestTemplate restTemplate;
  private ExternalSearchService externalSearchService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private ObjectNode openLibraryResponse;
  private ObjectNode googleBooksResponse;

  @BeforeEach
  void setUp() throws Exception {
    restTemplate = mock(RestTemplate.class);
    externalSearchService = new ExternalSearchService();

    Field restTemplateField = ExternalSearchService.class.getDeclaredField("restTemplate");
    restTemplateField.setAccessible(true);
    restTemplateField.set(externalSearchService, restTemplate);

    ObjectNode doc = objectMapper.createObjectNode();
    doc.put("title", "1984");
    doc.put("first_publish_year", 1949);
    ArrayNode authorName = doc.putArray("author_name");
    authorName.add("George Orwell");

    openLibraryResponse = objectMapper.createObjectNode();
    ArrayNode docs = openLibraryResponse.putArray("docs");
    docs.add(doc);

    ObjectNode volumeInfo = objectMapper.createObjectNode();
    volumeInfo.put("title", "1984");
    volumeInfo.put("publishedDate", "1949-06-08");
    ArrayNode authors = volumeInfo.putArray("authors");
    authors.add("George Orwell");

    ObjectNode item = objectMapper.createObjectNode();
    item.set("volumeInfo", volumeInfo);

    googleBooksResponse = objectMapper.createObjectNode();
    googleBooksResponse.put("totalItems", 1);
    ArrayNode items = googleBooksResponse.putArray("items");
    items.add(item);
  }

  @Test
  void findBookByISBN_shouldReturnFromOpenLibrary() throws BadGatewayException {
    when(restTemplate.getForEntity(contains("openlibrary.org"), eq(JsonNode.class)))
        .thenReturn(new ResponseEntity<>(openLibraryResponse, HttpStatus.OK));

    ExternalSearchResponse result = externalSearchService.findBookByISBN("9780451524935");

    assertEquals("OpenLibrary", result.getProvider());
    assertEquals("9780451524935", result.getIsbn());
    assertEquals("1984", result.getTitle());
    assertEquals(Year.of(1949), result.getFirstPublishYear());
    assertEquals(1, result.getAuthors().size());
    assertEquals("George Orwell", result.getAuthors().getFirst());
  }

  @Test
  void findBookByISBN_shouldFallbackToGoogleBooks() throws BadGatewayException {
    when(restTemplate.getForEntity(contains("openlibrary.org"), eq(JsonNode.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
    when(restTemplate.getForEntity(contains("googleapis.com"), eq(JsonNode.class)))
        .thenReturn(new ResponseEntity<>(googleBooksResponse, HttpStatus.OK));

    ExternalSearchResponse result = externalSearchService.findBookByISBN("9780451524935");

    assertEquals("Google Books", result.getProvider());
    assertEquals("9780451524935", result.getIsbn());
    assertEquals("1984", result.getTitle());
    assertEquals(1, result.getAuthors().size());
    assertEquals("George Orwell", result.getAuthors().getFirst());
  }

  @Test
  void findBookByISBN_shouldThrowResourceNotFound() {
    when(restTemplate.getForEntity(contains("openlibrary.org"), eq(JsonNode.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
    when(restTemplate.getForEntity(contains("googleapis.com"), eq(JsonNode.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

    assertThrows(
        ResourceNotFoundException.class,
        () -> externalSearchService.findBookByISBN("0000000000000"));
  }

  @Test
  void findBookByISBN_shouldThrowBadGateway() {
    when(restTemplate.getForEntity(contains("openlibrary.org"), eq(JsonNode.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
    when(restTemplate.getForEntity(contains("googleapis.com"), eq(JsonNode.class)))
        .thenThrow(new RestClientException("Service Unavailable"));

    assertThrows(
        BadGatewayException.class, () -> externalSearchService.findBookByISBN("9780451524935"));
  }
}
