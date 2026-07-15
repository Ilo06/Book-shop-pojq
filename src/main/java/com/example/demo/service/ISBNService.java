package com.example.demo.service;

import com.example.demo.dto.response.ISBNSearchResponse;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.NotFoundException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ISBNService {
  private final String openLibraryURL = "https://openlibrary.org";

  public ISBNSearchResponse findISBNByISBN(String isbn) {
    JsonNode node =
        new RestTemplate()
            .getForObject(
                openLibraryURL
                    + "/search.json?q="
                    + isbn
                    + "&fields=title,author_name,first_publish_year",
                JsonNode.class);
    if (node != null && node.has("docs")) {
      ISBNSearchResponse response = new ISBNSearchResponse();

      JsonNode data = node.path("docs").get(0);
      response.setIsbn(isbn);

      if (!data.get("title").isNull() && data.get("title").isTextual()) {
        response.setTitle(data.path("title").asText());
      }

      if (!data.get("first_publish_year").isNull() && data.get("first_publish_year").isInt()) {
        response.setFirstPublishYear(Year.of(data.path("first_publish_year").asInt()));
      }

      List<String> authors = new ArrayList<>();
      if (!data.path("author_name").isNull() && data.path("author_name").isArray()) {
        data.path("author_name").forEach(authorNode -> authors.add(authorNode.asText()));
      }
      response.setAuthors(authors);

      return response;
    }
    throw new NotFoundException("Failed to find ISBN:" + isbn);
  }
}
