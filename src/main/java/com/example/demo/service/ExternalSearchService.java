package com.example.demo.service;

import com.example.demo.dto.response.ExternalSearchResponse;
import com.example.demo.exception.BadGatewayException;
import com.example.demo.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalSearchService {
  private final RestTemplate restTemplate = new RestTemplate();
  private final String apiKey =
      (System.getenv("GOOGLE_BOOKS_API_KEY") != null
              && !System.getenv("GOOGLE_BOOKS_API_KEY").isBlank())
          ? System.getenv("GOOGLE_BOOKS_API_KEY")
          : System.getProperty("GOOGLE_BOOKS_API_KEY");

  public ExternalSearchResponse findBookByISBN(String isbn) throws BadGatewayException {
    Optional<ExternalSearchResponse> response = this.searchOpenLibrary(isbn);
    if (response.isPresent()) {
      return response.get();
    }

    response = this.searchGoogleBook(isbn);
    if (response.isPresent()) {
      return response.get();
    }

    throw new ResourceNotFoundException("Not found");
  }

  private Optional<ExternalSearchResponse> searchOpenLibrary(String isbn) {
    String openLibraryURL = "https://openlibrary.org";
    ResponseEntity<?> responseEntity =
        restTemplate.getForEntity(
            openLibraryURL
                + "/search.json?q="
                + isbn
                + "&fields=title,author_name,first_publish_year",
            JsonNode.class);
    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
      return Optional.empty();
    }

    JsonNode book = (JsonNode) responseEntity.getBody();
    if (isNodeNotNull(book) && book.has("docs")) {

      ExternalSearchResponse response = new ExternalSearchResponse();

      JsonNode data = book.path("docs").get(0);
      if (!isNodeNotNull(data)) {
        return Optional.empty();
      }

      response.setProvider("OpenLibrary");
      response.setIsbn(isbn);

      if (isNodeNotNull(data.get("title")) && data.get("title").isTextual()) {
        response.setTitle(data.path("title").asText());
      }

      if (isNodeNotNull(data.get("first_publish_year"))
          && (data.get("first_publish_year").isTextual()
              || data.get("first_publish_year").isInt())) {
        response.setFirstPublishYear(parseBookPublishYear(data.get("first_publish_year").asText()));
      }

      List<String> authors = new ArrayList<>();
      if (isNodeNotNull(data.get("author_name")) && data.path("author_name").isArray()) {
        data.path("author_name").forEach(authorNode -> authors.add(authorNode.asText()));
      }
      response.setAuthors(authors);

      return Optional.of(response);
    }
    return Optional.empty();
  }

  private Optional<ExternalSearchResponse> searchGoogleBook(String isbn)
      throws BadGatewayException {
    String googleBooksURL = "https://www.googleapis.com/books/v1";

    JsonNode responseJson = null;

    // As Google Book API seems to be quite unpredictable
    // -> brute forcing 2 retries with 500ms-1s backoff before throwing error
    for (int i = 1; i <= 3; i++) {
      try {
        ResponseEntity<?> responseEntity =
            restTemplate.getForEntity(
                googleBooksURL + "/volumes?maxResults=1&q=isbn:" + isbn + "&key=" + apiKey,
                JsonNode.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
          responseJson = (JsonNode) responseEntity.getBody();
          break;
        }
      } catch (RestClientException e) {
        if (i == 3) {
          throw new BadGatewayException("Google Books Service Unavailable");
        } else {
          try {
            Thread.sleep(500 * i);
          } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException();
          }
        }
      }
    }

    if (!isNodeNotNull(responseJson) || (responseJson.get("totalItems").asInt() == 0)) {
      return Optional.empty();
    }

    ExternalSearchResponse searchResponse = new ExternalSearchResponse();

    JsonNode data = responseJson.path("items").get(0);
    if (!isNodeNotNull(data)) {
      return Optional.empty();
    }

    searchResponse.setProvider("Google Books");
    searchResponse.setIsbn(isbn);

    JsonNode volumeInfo = data.get("volumeInfo");
    if (!isNodeNotNull(volumeInfo)) {
      return Optional.empty();
    }

    if (isNodeNotNull(volumeInfo.get("title")) && volumeInfo.get("title").isTextual()) {
      searchResponse.setTitle(volumeInfo.path("title").asText());
    }

    if (isNodeNotNull(volumeInfo.get("publishedDate"))
        && volumeInfo.get("publishedDate").isTextual()) {
      searchResponse.setFirstPublishYear(
          parseBookPublishYear(volumeInfo.get("publishedDate").asText()));
    }

    List<String> authors = new ArrayList<>();
    if (isNodeNotNull(volumeInfo.get("authors")) && volumeInfo.path("authors").isArray()) {
      volumeInfo.path("authors").forEach(authorNode -> authors.add(authorNode.asText()));
    }
    searchResponse.setAuthors(authors);

    return Optional.of(searchResponse);
  }

  private Year parseBookPublishYear(String date) {
    if (date == null || date.isBlank()) {
      return null;
    }
    if (date.contains("-")) {
      return Year.of(Integer.parseInt(date.split("-")[0]));
    }
    return Year.parse(date);
  }

  private boolean isNodeNotNull(JsonNode data) {
    return (data != null && !data.isNull());
  }
}
